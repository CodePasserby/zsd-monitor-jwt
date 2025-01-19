package com.example.websocket;

import com.example.entity.dto.ClientDetail;
import com.example.entity.dto.ClientSsh;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientSshMapper;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ServerEndpoint("/terminal/{clientId}") // WebSocket端点，接收客户端ID作为路径参数
public class TerminalWebSocket {

    // 注入数据库映射器，用于查询客户端详情和SSH配置
    private static ClientDetailMapper detailMapper;

    @Resource
    public void setDetailMapper(ClientDetailMapper detailMapper) {
        TerminalWebSocket.detailMapper = detailMapper;
    }

    private static ClientSshMapper sshMapper;

    @Resource
    public void setSshMapper(ClientSshMapper sshMapper) {
        TerminalWebSocket.sshMapper = sshMapper;
    }

    // 用于存储每个WebSocket连接与其对应的Shell连接
    private static final Map<Session, Shell> sessionMap = new ConcurrentHashMap<>();
    // 使用单线程池来异步处理Shell的读取操作
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    /**
     * 当新的WebSocket连接打开时调用
     * @param session 当前会话
     * @param clientId 客户端ID
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "clientId") String clientId) throws Exception {
        // 获取客户端的详细信息和SSH配置
        ClientDetail detail = detailMapper.selectById(clientId);
        ClientSsh ssh = sshMapper.selectById(clientId);

        // 如果客户端信息或SSH配置为空，关闭连接并返回错误信息
        if (detail == null || ssh == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "无法识别此主机"));
            return;
        }

        // 尝试创建SSH连接
        if (this.createSshConnection(session, ssh, detail.getIp())) {
            log.info("主机 {} 的SSH连接已创建", detail.getIp());
        }
    }

    /**
     * 当WebSocket接收到消息时调用
     * @param session 当前会话
     * @param message 接收到的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // 获取与当前会话对应的Shell实例，并将消息写入Shell的输出流
        Shell shell = sessionMap.get(session);
        OutputStream output = shell.output;
        output.write(message.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    /**
     * 当WebSocket连接关闭时调用
     * @param session 当前会话
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        // 获取当前会话对应的Shell实例，并关闭Shell连接
        Shell shell = sessionMap.get(session);
        if (shell != null) {
            shell.close();
            sessionMap.remove(session);
            log.info("主机 {} 的SSH连接已断开", shell.js.getHost());
        }
    }

    /**
     * 当WebSocket连接发生错误时调用
     * @param session 当前会话
     * @param error 错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error("用户WebSocket连接出现错误", error);
        session.close();
    }

    /**
     * 创建SSH连接
     * @param session 当前WebSocket会话
     * @param ssh 客户端的SSH配置
     * @param ip 客户端的IP地址
     * @return 是否成功创建SSH连接
     */
    private boolean createSshConnection(Session session, ClientSsh ssh, String ip) throws IOException {
        try {
            // 使用JSch库创建SSH连接
            JSch jSch = new JSch();
            com.jcraft.jsch.Session js = jSch.getSession(ssh.getUsername(), ip, ssh.getPort());
            js.setPassword(ssh.getPassword());
            js.setConfig("StrictHostKeyChecking", "no");
            js.setTimeout(3000);
            js.connect();

            // 打开一个Shell通道
            ChannelShell channel = (ChannelShell) js.openChannel("shell");
            channel.setPtyType("xterm");
            channel.connect(1000);

            // 将Shell连接保存到会话映射中
            sessionMap.put(session, new Shell(session, js, channel));
            return true;
        } catch (JSchException e) {
            // 根据错误信息关闭连接并返回具体的错误信息
            String message = e.getMessage();
            if (message.equals("Auth fail")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "登录SSH失败，用户名或密码错误"));
                log.error("连接SSH失败，用户名或密码错误，登录失败");
            } else if (message.contains("Connection refused")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "连接被拒绝，可能是没有启动SSH服务或是放开端口"));
                log.error("连接SSH失败，连接被拒绝，可能是没有启动SSH服务或是放开端口");
            } else {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, message));
                log.error("连接SSH时出现错误", e);
            }
        }
        return false;
    }

    /**
     * Shell类封装SSH的输入输出流
     */
    private class Shell {
        private final Session session;
        private final com.jcraft.jsch.Session js;
        private final ChannelShell channel;
        private final InputStream input;
        private final OutputStream output;

        public Shell(Session session, com.jcraft.jsch.Session js, ChannelShell channel) throws IOException {
            this.js = js;
            this.session = session;
            this.channel = channel;
            this.input = channel.getInputStream();
            this.output = channel.getOutputStream();

            // 启动一个线程异步读取Shell输出
            service.submit(this::read);
        }

        /**
         * 读取Shell的输入流，并将内容发送到WebSocket客户端
         */
        private void read() {
            try {
                byte[] buffer = new byte[1024 * 1024];
                int i;
                while ((i = input.read(buffer)) != -1) {
                    String text = new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8);
                    session.getBasicRemote().sendText(text);
                }
            } catch (Exception e) {
                log.error("读取SSH输入流时出现问题", e);
            }
        }

        /**
         * 关闭Shell连接
         */
        public void close() throws IOException {
            input.close();
            output.close();
            channel.disconnect();
            js.disconnect();
            service.shutdown();
        }
    }
}

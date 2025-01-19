package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.dto.ClientSsh;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.mapper.ClientSshMapper;
import com.example.service.ClientService;
import com.example.utils.InfluxDbUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken = this.generateNewToken(); // 用于客户端注册的令牌

    // 客户端ID缓存
    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();
    // 客户端Token缓存
    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();

    // 注入ClientDetailMapper，用于操作客户端详细信息
    @Resource
    ClientDetailMapper detailMapper;

    // 注入InfluxDbUtils，用于操作InfluxDB
    @Resource
    InfluxDbUtils influx;

    // 注入ClientSshMapper，用于操作客户端SSH信息
    @Resource
    ClientSshMapper sshMapper;

    // 在构造时初始化缓存
    @PostConstruct
    public void initClientCache() {
        clientTokenCache.clear();
        clientIdCache.clear();
        // 加载所有客户端信息到缓存中
        this.list().forEach(this::addClientCache);
    }

    @Override
    public String registerToken() {
        return registerToken; // 返回当前的注册令牌
    }

    @Override
    public Client findClientById(int id) {
        return clientIdCache.get(id); // 根据客户端ID查找客户端
    }

    @Override
    public Client findClientByToken(String token) {
        return clientTokenCache.get(token); // 根据Token查找客户端
    }

    @Override
    public boolean verifyAndRegister(String token) {
        // 验证令牌并注册客户端
        if (this.registerToken.equals(token)) {
            int id = this.randomClientId(); // 生成随机客户端ID
            Client client = new Client(id, "未命名主机", token, "cn", "未命名节点", new Date());
            // 保存客户端并更新缓存
            if (this.save(client)) {
                registerToken = this.generateNewToken(); // 更新注册令牌
                this.addClientCache(client); // 将客户端信息加入缓存
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateClientDetail(ClientDetailVO vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo, detail); // 将ClientDetailVO的数据复制到ClientDetail中
        detail.setId(client.getId()); // 设置客户端ID
        // 更新或插入客户端详细信息
        if(Objects.nonNull(detailMapper.selectById(client.getId()))) {
            detailMapper.updateById(detail); // 更新
        } else {
            detailMapper.insert(detail); // 插入
        }
    }

    private final Map<Integer, RuntimeDetailVO> currentRuntime = new ConcurrentHashMap<>();

    @Override
    public void updateRuntimeDetail(RuntimeDetailVO vo, Client client) {
        currentRuntime.put(client.getId(), vo); // 更新客户端的运行时详细信息
        influx.writeRuntimeData(client.getId(), vo); // 写入InfluxDB
    }

    @Override
    public List<ClientPreviewVO> listClients() {
        return clientIdCache.values().stream().map(client -> {
            // 构建客户端预览信息
            ClientPreviewVO vo = client.asViewObject(ClientPreviewVO.class);
            BeanUtils.copyProperties(detailMapper.selectById(vo.getId()), vo); // 拷贝详细信息
            RuntimeDetailVO runtime = currentRuntime.get(client.getId());
            if(this.isOnline(runtime)) {
                // 如果客户端在线，复制运行时信息
                BeanUtils.copyProperties(runtime, vo);
                vo.setOnline(true);
            }
            return vo;
        }).toList();
    }

    @Override
    public List<ClientSimpleVO> listSimpleList() {
        return clientIdCache.values().stream().map(client -> {
            // 构建简单的客户端信息
            ClientSimpleVO vo = client.asViewObject(ClientSimpleVO.class);
            BeanUtils.copyProperties(detailMapper.selectById(vo.getId()), vo); // 拷贝详细信息
            return vo;
        }).toList();
    }

    @Override
    public void renameClient(RenameClientVO vo) {
        // 更新客户端名称
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));
        this.initClientCache(); // 刷新缓存
    }

    @Override
    public void renameNode(RenameNodeVO vo) {
        // 更新节点名称和位置
        this.update(Wrappers.<Client>update().eq("id", vo.getId())
                .set("node", vo.getNode()).set("location", vo.getLocation()));
        this.initClientCache(); // 刷新缓存
    }

    @Override
    public ClientDetailsVO clientDetails(int clientId) {
        ClientDetailsVO vo = this.clientIdCache.get(clientId).asViewObject(ClientDetailsVO.class);
        BeanUtils.copyProperties(detailMapper.selectById(clientId), vo); // 拷贝详细信息
        vo.setOnline(this.isOnline(currentRuntime.get(clientId))); // 设置客户端是否在线
        return vo;
    }

    @Override
    public RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId) {
        RuntimeHistoryVO vo = influx.readRuntimeData(clientId); // 获取客户端的运行时历史
        ClientDetail detail = detailMapper.selectById(clientId); // 获取客户端详细信息
        BeanUtils.copyProperties(detail, vo); // 拷贝详细信息
        return vo;
    }

    @Override
    public RuntimeDetailVO clientRuntimeDetailsNow(int clientId) {
        return currentRuntime.get(clientId); // 获取客户端当前的运行时详细信息
    }

    @Override
    public void deleteClient(int clientId) {
        this.removeById(clientId); // 删除客户端
        detailMapper.deleteById(clientId); // 删除客户端详细信息
        this.initClientCache(); // 刷新缓存
        currentRuntime.remove(clientId); // 移除运行时信息
    }

    @Override
    public void saveClientSshConnection(SshConnectionVO vo) {
        Client client = clientIdCache.get(vo.getId()); // 获取客户端信息
        if(client == null) return; // 如果客户端为空，返回
        ClientSsh ssh = new ClientSsh();
        BeanUtils.copyProperties(vo, ssh); // 拷贝SSH连接信息
        if(Objects.nonNull(sshMapper.selectById(client.getId()))) {
            sshMapper.updateById(ssh); // 更新
        } else {
            sshMapper.insert(ssh); // 插入
        }
    }

    @Override
    public SshSettingsVO sshSettings(int clientId) {
        ClientDetail detail = detailMapper.selectById(clientId); // 获取客户端详细信息
        ClientSsh ssh = sshMapper.selectById(clientId); // 获取客户端SSH信息
        SshSettingsVO vo;
        if(ssh == null) {
            vo = new SshSettingsVO(); // 如果没有SSH信息，创建新对象
        } else {
            vo = ssh.asViewObject(SshSettingsVO.class); // 将SSH信息转换为VO对象
        }
        vo.setIp(detail.getIp()); // 设置IP
        return vo;
    }

    private boolean isOnline(RuntimeDetailVO runtime) {
        // 判断客户端是否在线（根据时间戳判断是否在一分钟内）
        return runtime != null && System.currentTimeMillis() - runtime.getTimestamp() < 60 * 1000;
    }

    private void addClientCache(Client client) {
        // 将客户端信息加入缓存
        clientIdCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }

    private int randomClientId() {
        // 生成随机客户端ID
        return new Random().nextInt(90000000) + 10000000;
    }

    private String generateNewToken() {
        // 生成新的注册令牌
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return sb.toString();
    }
}

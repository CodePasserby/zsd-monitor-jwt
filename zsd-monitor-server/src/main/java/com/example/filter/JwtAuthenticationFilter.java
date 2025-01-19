package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.Client;
import com.example.service.AccountService;
import com.example.service.ClientService;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 用于从请求头中获取JWT令牌并进行校验，若校验通过，将用户信息放入请求上下文中，
 * 并为后续的请求提供用户的验证信息。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils utils;

    @Resource
    ClientService service;

    /**
     * 处理HTTP请求，校验JWT令牌并进行相应处理
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤链
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization字段（JWT令牌）
        String authorization = request.getHeader("Authorization");
        // 获取请求的URI
        String uri = request.getRequestURI();

        // 如果请求路径以"/monitor"开头且不是注册请求，则进行客户端验证
        if(uri.startsWith("/monitor")) {
            if(!uri.endsWith("/register")) {
                // 根据JWT令牌查找客户端信息
                Client client = service.findClientByToken(authorization);
                if(client == null) {
                    // 客户端未注册，返回401错误
                    response.setStatus(401);
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(RestBean.failure(401, "未注册").asJsonString());
                    return;
                } else {
                    // 客户端验证通过，将客户端信息存放在请求属性中
                    request.setAttribute(Const.ATTR_CLIENT, client);
                }
            }
        } else {
            // 处理用户请求，解析JWT令牌
            DecodedJWT jwt = utils.resolveJwt(authorization);
            if(jwt != null) {
                // 将JWT解析成UserDetails对象并设置到Spring Security上下文中
                UserDetails user = utils.toUser(jwt);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 将用户ID和角色存放在请求属性中
                request.setAttribute(Const.ATTR_USER_ID, utils.toId(jwt));
                request.setAttribute(Const.ATTR_USER_ROLE, new ArrayList<>(user.getAuthorities()).get(0).getAuthority());

                // 针对终端请求路径进行权限校验
                if(request.getRequestURI().startsWith("/terminal/") && !accessShell(
                        (int) request.getAttribute(Const.ATTR_USER_ID),
                        (String) request.getAttribute(Const.ATTR_USER_ROLE),
                        Integer.parseInt(request.getRequestURI().substring(10)))) {
                    // 如果没有访问权限，返回401错误
                    response.setStatus(401);
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(RestBean.failure(401, "无权访问").asJsonString());
                    return;
                }
            }
        }
        // 继续过滤链的执行
        filterChain.doFilter(request, response);
    }

    @Resource
    AccountService accountService;

    /**
     * 校验用户是否有权限访问终端
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param clientId 客户端ID
     * @return 是否有访问权限
     */
    private boolean accessShell(int userId, String userRole, int clientId) {
        // 如果用户是管理员，直接返回有权限
        if(Const.ROLE_ADMIN.equals(userRole.substring(5))) {
            return true;
        } else {
            // 如果是普通用户，检查用户是否在客户端列表中
            Account account = accountService.getById(userId);
            return account.getClientList().contains(clientId);
        }
    }
}

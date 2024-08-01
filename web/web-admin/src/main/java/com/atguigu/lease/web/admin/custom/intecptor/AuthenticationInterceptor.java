package com.atguigu.lease.web.admin.custom.intecptor;

import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ClassName: AuthenticationInterceptor
 * PackageName: com.atguigu.lease.web.admin.custom.interceptor
 * Create: 2024/7/31-15:36
 * Description: 配置拦截器，在用户调用controller方法前验证token
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("access-token");
        Claims claims = JwtUtil.parseToken(token);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(claims.get("userId", Long.class));
        loginUser.setUserName(claims.get("username", String.class));
        LoginUserHolder.setLoginUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 这个方法完成后又会有新的请求进来，所以为了避免重复在ThreadLocal里面赋值，要先把原来的给销毁了
        LoginUserHolder.clear();
    }
}
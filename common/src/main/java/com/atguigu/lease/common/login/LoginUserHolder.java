package com.atguigu.lease.common.login;

/**
 * ClassName: LoginUserHolder
 * PackageName: com.atguigu.lease.common.login
 * Create: 2024/8/1-10:02
 * Description:将token中的用户信息存储到ThreadLocal中
 */
public class LoginUserHolder {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    public static void setLoginUser(LoginUser loginUser) {
         threadLocal.set(loginUser);
    }
    public static LoginUser getLoginUser(){
        return threadLocal.get();
    }
    public static void clear(){
        threadLocal.remove();
    }
}

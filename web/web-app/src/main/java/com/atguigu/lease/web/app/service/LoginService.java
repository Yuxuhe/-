package com.atguigu.lease.web.app.service;

public interface LoginService {

    void sendCode(String phone, String verifyCode);
}

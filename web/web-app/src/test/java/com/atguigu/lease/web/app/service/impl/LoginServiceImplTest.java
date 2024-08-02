package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.web.app.service.LoginService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: LoginServiceImplTest
 * PackageName: com.atguigu.lease.web.app.service.impl
 * Create: 2024/8/1-15:46
 * Description: 测试短信发送
 */
@SpringBootTest
class LoginServiceImplTest {
    @Resource
    private LoginService loginService;

    @Test
    public void testSendCode(){
        loginService.sendCode("18628022186","123456");
    }

}
package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.atguigu.lease.web.app.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private Client client;

    @Override
    public void sendCode(String phone, String code) {
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setPhoneNumbers(phone);
        smsRequest.setSignName("阿里云短信测试");
        smsRequest.setTemplateCode("SMS_154950909"); // 模板编号
        smsRequest.setTemplateParam("{\"code\":\"" + code + "\"}"); // 将占位符的对应关系转换成json格式，{"code":"123456"}

        try {
            client.sendSms(smsRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

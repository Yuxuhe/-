package com.atguigu.lease.common.sms;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: AliyunSmsConfiguration
 * PackageName: com.atguigu.lease.common.sms
 * Create: 2024/8/1-15:25
 * Description: 配置阿里云短信服务
 */
@Configuration
@EnableConfigurationProperties(AliyunSMSProperties.class)
@ConditionalOnProperty(name = "aliyun.sms.endpoint")
public class AliyunSmsConfiguration {

    @Resource
    private AliyunSMSProperties properties;

    // 配置client
    @Bean
    public Client smsClient(){
        Config config = new Config();
        config.setAccessKeyId(properties.getAccessKeyId());
        config.setAccessKeySecret(properties.getAccessKeySecret());
        config.setEndpoint(properties.getEndpoint());
        try {
            return new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

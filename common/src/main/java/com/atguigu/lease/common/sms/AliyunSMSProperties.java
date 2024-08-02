package com.atguigu.lease.common.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName: AliyunSMSProperties
 * PackageName: com.atguigu.lease.common.sms
 * Create: 2024/8/1-15:23
 * Description: 短信服务所需要的各个属性字段
 */
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class AliyunSMSProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;
}

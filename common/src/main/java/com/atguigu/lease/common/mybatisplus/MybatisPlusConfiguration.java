package com.atguigu.lease.common.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: MybatisPlusConfiguration
 * PackageName: com.atguigu.lease.common.mybatisplus
 * Create: 2024/7/22-10:37
 * Description:
 */
@Configuration
@MapperScan("com.atguigu.lease.web.*.mapper")
public class MybatisPlusConfiguration {
}

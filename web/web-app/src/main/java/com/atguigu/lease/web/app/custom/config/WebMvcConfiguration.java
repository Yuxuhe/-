package com.atguigu.lease.web.app.custom.config;

import com.atguigu.lease.web.app.custom.interceptor.AuthenticationInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: WebMvcConfiguration
 * PackageName: com.atguigu.lease.web.app.custom.config
 * Create: 2024/8/2-14:50
 * Description: 配置类
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private AuthenticationInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/app/**").excludePathPatterns("/app/login/**");
    }
}

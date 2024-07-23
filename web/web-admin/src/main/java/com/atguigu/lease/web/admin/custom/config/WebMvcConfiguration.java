package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.converter.StringToItemTypeConverter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: WebMvcConfiguration
 * PackageName: com.atguigu.lease.web.admin.custom.config
 * Create: 2024/7/23-9:23
 * Description:
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private StringToBaseEnumConverterFactory converterFactory;
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(converterFactory);
    }
}

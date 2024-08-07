package com.atguigu.lease.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * ClassName: RedisConfiguration
 * PackageName: com.atguigu.lease.common.redis
 * Create: 2024/8/6-11:48
 * Description: 自定义RedisTemplate
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String,Object> stringObjectRedisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> stringObjectRedisTemplate = new RedisTemplate<>();
        stringObjectRedisTemplate.setConnectionFactory(factory);
        stringObjectRedisTemplate.setKeySerializer(RedisSerializer.string());
        stringObjectRedisTemplate.setValueSerializer(RedisSerializer.java());
        return stringObjectRedisTemplate;
    }
}

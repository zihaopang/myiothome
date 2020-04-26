package com.myiothome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式为String
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式为Json,因为value的形式多种多样
        template.setValueSerializer(RedisSerializer.json());
        //设置HashKey的序列化方式为String
        template.setHashKeySerializer(RedisSerializer.string());
        //设置HashValue的序列化方式为Json
        template.setHashValueSerializer(RedisSerializer.json());

        //让设置生效
        template.afterPropertiesSet();

        return template;
    }
}

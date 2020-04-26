package com.myiothome.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 产生验证码的bean
 * */
@Configuration
public class KaptchaConfig {

    /*像Spring注入bean*/
    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        //验证码参数配置
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","33");
        properties.setProperty("kaptcha.textproducer.font.size","30");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}

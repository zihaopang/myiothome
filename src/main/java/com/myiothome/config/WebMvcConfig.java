package com.myiothome.config;

import com.myiothome.annotation.LoginRequired;
import com.myiothome.controller.interceptor.DataInterceptor;
import com.myiothome.controller.interceptor.LoginRequiredInterceptor;
import com.myiothome.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //织入两个拦截器
//    @Autowired
//    LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    DataInterceptor dataInterceptor;

    /*
    注册要有顺序，先要获取loginTicketInterceptor中的ticket，然后再注册拦截器loginRequiredInterceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).
                excludePathPatterns("/**/.css", "/**/*.js", "/**/*.png", "/**/*.jpeg", "/**/*.jpg");
        registry.addInterceptor(dataInterceptor).
                excludePathPatterns("/**/.css", "/**/*.js", "/**/*.png", "/**/*.jpeg", "/**/*.jpg");
//        registry.addInterceptor(loginRequiredInterceptor).
//                excludePathPatterns("/**/.css","/**/*.js","/**/*.png","/**/*.jpeg","/**/*.jpg");
    }

}

package com.myiothome.controller.interceptor;

import com.myiothome.entity.User;
import com.myiothome.service.DataService;
import com.myiothome.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.UnknownServiceException;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    DataService dataService;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        User user = hostHolder.getUser();

        if(user != null){
            dataService.setUv(ip);
            dataService.setDau(user.getId());
        }

        return true;
    }
}

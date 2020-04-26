package com.myiothome.controller.interceptor;

import com.myiothome.entity.LoginTicket;
import com.myiothome.entity.User;
import com.myiothome.service.LoginTicketService;
import com.myiothome.service.UserService;
import com.myiothome.util.CookieUtil;
import com.myiothome.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketService loginTicketService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getCookies(request,"ticket");
        if(ticket != null){
            LoginTicket loginTicket = loginTicketService.findLoginTicket(ticket);

            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpireTime().after(new Date())){
                User user = userService.findUserById(loginTicketService.findUserId(ticket));
                hostHolder.setUser(user);
                //储存spring security登陆凭证
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(),userService.getAuthorities(user.getId())
                );
                System.out.println(userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView!= null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}

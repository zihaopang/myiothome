package com.myiothome.config;

import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements MyIotHomeConstent {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");//静态资源都可不拦截
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权处理
        http.authorizeRequests()
                .antMatchers(
                        "/comment/**",
                        "/discuss/add",
                        "/notice/**",
                        "/focus",
                        "/unfocus",
                        "/like",
                        "/message/**",
                        "/setting/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODEARTOR,
                        AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
                        AUTHORITY_MODEARTOR
                ).antMatchers(
                    "/discuss/delete",
                    "/data/**",
                    "/actuator/**"
                ).hasAnyAuthority(
                    AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登陆
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(MyIotHomeUtils.getJSONString(403, "你还没有登陆！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不够
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(MyIotHomeUtils.getJSONString(403, "你的权限不足！"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        /**
         * 推出登陆，spring security会认为所有logout路径的都是退出登录操作
         * 只要写一个新的路劲，就能覆盖原有路径,才能写我们自己的退出代码
         */
        http.logout().logoutUrl("/securityLogout");
    }
}

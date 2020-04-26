package com.myiothome.controller;

import com.google.code.kaptcha.Producer;
import com.myiothome.entity.User;
import com.myiothome.service.UserService;
import com.myiothome.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements MyIotHomeConstent {
    public static  final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @Autowired
    Producer kaptchaProducer;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CheckLoginStatus checkLoginStatus;

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpSession session,HttpServletResponse response){

        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        System.out.println("验证码："+text);
        //session暂存验证码
        session.setAttribute("kaptcha",text);

        response.setContentType("image/png");

        try(OutputStream os = response.getOutputStream()){
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            logger.error("验证码响应失败"+e.getMessage());
        }
    }

    @RequestMapping(path = "/login")
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model,HttpSession session, HttpServletResponse response)
    {
        //检查是否存在cookie如果有直接转到主页
        if(checkLoginStatus.checkLogin()){
            return "redirect:/index";
        }
        //对比验证码
        String kaptcha = (String)session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("errorMsg","验证码错误");
        }

        Map<String,Object> map = new HashMap<>();
        System.out.println(rememberMe+"+rememberMe");
        int expiredSeconds = rememberMe?ONE_MONTH:ONE_DAY;//cookie失效的时间
        map = userService.login(username,password,expiredSeconds);

        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);//cookie作用范围整个项目
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }
        else{
            model.addAttribute("errorMsg",map.get("errorMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletResponse response, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("ticket")){
                cookie.setPath(contextPath);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }

}

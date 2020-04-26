package com.myiothome.controller;

import com.myiothome.entity.User;
import com.myiothome.service.UserService;
import com.myiothome.util.CheckLoginStatus;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RegisterController implements MyIotHomeConstent {

    @Autowired
    UserService userService;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${myiothome.path.domain}")
    String domain;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CheckLoginStatus checkLoginStatus;
    @RequestMapping(path="/register")
    public String register(Model model,User user,String confirmPassword){
        //判断是否登陆，登陆直接跳转主页
        if(checkLoginStatus.checkLogin()){
            return "redirect:/index";
        }
        Map<String,Object> map = new HashMap<>();
        System.out.println(user);
        map = userService.register(user,confirmPassword);
        if(map == null || map.isEmpty()) {//注册成功,此处遇到问题
            //String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
            model.addAttribute("msg",user.getUsername()+",您已注册成功！");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else {
            System.out.println(map.get("errorMsg"));

            model.addAttribute("errorMsg",map.get("errorMsg"));

            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{activationCode}",method = RequestMethod.GET)
    public String activation(Model model,@PathVariable("userId") int userId, @PathVariable("activationCode") String activationCode){
        User user = userService.findUserById(userId);
        String activation = user.getActivationCode();

        if(!activation.equals(activationCode)){
            model.addAttribute("msg","参数非法！");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }
        else{
            userService.updateStatus(userId,ACTIVATED);//激活用户
            model.addAttribute("msg","激活成功，正在跳转...");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }
    }
}

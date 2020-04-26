package com.myiothome.service;

import com.myiothome.dao.LoginTickerMapper;
import com.myiothome.entity.LoginTicket;
import com.myiothome.entity.User;
import com.myiothome.dao.UserMapper;
import com.myiothome.util.MailClient;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements MyIotHomeConstent {
    @Autowired
    UserMapper userMapper;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${myiothome.path.domain}")
    String domain;
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    LoginTickerMapper loginTickerMapper;

    public User findUserById(int id){
        return userMapper.selectUserById(id);
    }

    public User findUserByName(String name){
        return userMapper.selectUserByName(name);
    }

    public User findUserByEmail(String email){
        return userMapper.selectUserByEmail(email);
    }

    public int updateStatus(int id,int status){
        return userMapper.updateUserStatus(id,status);
    }

    public int updateUsername(int id,String username){
        return userMapper.updateUserName(id,username);
    }

    public int updateEmail(int id,String email){
        return userMapper.updateUserEmail(id,email);
    }


    public int updateUserHeaderUrl(int id,String headerUrl){
        return userMapper.updateUserHeaderUrl(id,headerUrl);
    }
    public Map<String,Object> register(User user,String confirmPassword){
        Map<String ,Object> map = new HashMap<>();

        if(StringUtils.isBlank(user.getUsername())){
            map.put("errorMsg","用户名不能为空！");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("errorMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("errorMsg","邮箱不能为空！");
            return map;
        }
        if(!confirmPassword.equals(user.getPassword())){
            map.put("errorMsg","密码输入不一致！");
            return map;
        }

        User u = userMapper.selectUserByName(user.getUsername());

        if(u != null){
            map.put("errorMsg","此用户名已存在！");
            return map;
        }

//        u = userMapper.selectUserByEmail(user.getEmail());
//        if(u != null) {
//            map.put("errorMsg", "此邮箱已存在！");
//            return map;
//        }

        //开始新建用户
        user.setSalt(MyIotHomeUtils.generateUUID().substring(0,5));
        user.setPassword(MyIotHomeUtils.md5(user.getSalt()+user.getPassword()));
        user.setStatus(0);
        user.setActivationCode(MyIotHomeUtils.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活邮件:http://localhost:8080/myiothome/activation/userid/activation_code
        String path = domain+contextPath+"/activation/"+userMapper.selectUserByName(user.getUsername()).getId()+"/"+user.getActivationCode();
        context.setVariable("url",path);
        //模板渲染成html
        String content = templateEngine.process("/mail/activation",context);
        //发送邮件
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    /*
    登陆服务
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String ,Object> map = new HashMap<>();
        User u = userMapper.selectUserByName(username);

        if(u == null){
            map.put("errorMsg","用户名错误");
            return map;
        }
        //顺序要注意
        String hexCode = MyIotHomeUtils.md5(u.getSalt()+password);
        if(!hexCode.equals(u.getPassword())){
            map.put("errorMsg","密码错误！");
            return map;
        }

        if(u.getStatus() == NO_ACTIVATE){
            map.put("errorMsg","该用户尚未激活！");
            return map;
        }

        //创建新的loginTicket
        String ticket = MyIotHomeUtils.generateUUID();//产生ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setExpireTime(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicket.setStatus(0);
        loginTicket.setTicket(ticket);

        loginTickerMapper.insertLoginTicket(loginTicket);

        map.put("ticket",ticket);
        return map;
    }

    //得到用户的权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODEARTOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });

        return list;
    }

    //测试简化的Spring线程池,该方法可以在多线程的环境下，被异步调用
    @Async
    public void execute1(){
        System.out.println("简化的JDK多线程");
    }
}

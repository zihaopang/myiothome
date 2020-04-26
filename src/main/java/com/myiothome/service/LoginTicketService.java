package com.myiothome.service;

import com.myiothome.dao.LoginTickerMapper;
import com.myiothome.entity.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginTicketService {
    @Autowired
    LoginTickerMapper loginTickerMapper;

    public String findLoginTicket(int userId){
        return loginTickerMapper.getLoginTicket(userId);
    }

    public int findStatus(int userId){
        return loginTickerMapper.getStatus(userId);
    }

    public int updateUserTicket(int userId){
        return loginTickerMapper.setLoginTicket(userId);
    }

    public int updateUserStatus(int userId){
        return loginTickerMapper.setStatus(userId);
    }
    public int findUserId(String ticket){
        return loginTickerMapper.getUserId(ticket);
    }
    public int addLoginTicket(LoginTicket loginTicket){
        return loginTickerMapper.insertLoginTicket(loginTicket);
    }
    public  LoginTicket findLoginTicket(String ticket){
        return loginTickerMapper.getLogininTicket(ticket);
    }
}

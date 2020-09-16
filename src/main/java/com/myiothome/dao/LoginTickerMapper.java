package com.myiothome.dao;

import com.myiothome.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTickerMapper {
    int setLoginTicket(int userId);

    String getLoginTicket(int userId);

    int getStatus(int userId);

    int setStatus(int userId);

    int getUserId(String ticket);

    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket getLogininTicket(String ticket);
}

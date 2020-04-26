package com.myiothome.entity;

import java.util.Date;

public class LoginTicket {
    int userId;
    String ticket;
    int status;//'0-有效; 1-无效;
    Date expireTime;

    public int getUserId() {
        return userId;
    }

    public LoginTicket setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getTicket() {
        return ticket;
    }

    public LoginTicket setTicket(String ticket) {
        this.ticket = ticket;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public LoginTicket setStatus(int status) {
        this.status = status;
        return this;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public LoginTicket setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expireTime=" + expireTime +
                '}';
    }
}

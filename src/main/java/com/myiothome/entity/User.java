package com.myiothome.entity;

import java.util.Date;

public class User {
    int id;
    String username;
    String password;
    String email;
    String salt;
    int status;//'0-未激活; 1-已激活;'
    String activationCode;
    String headerUrl;
    Date createTime;
    int type;//'0-普通用户; 1-超级管理员; 2-版主;'

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public User setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public User setActivationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public User setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public User setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public int getType() {
        return type;
    }

    public User setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", salt='" + salt + '\'' +
                ", status=" + status +
                ", activationCode='" + activationCode + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                '}';
    }
}

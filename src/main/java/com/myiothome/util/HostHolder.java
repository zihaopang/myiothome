package com.myiothome.util;

import com.myiothome.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}

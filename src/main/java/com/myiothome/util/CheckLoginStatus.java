package com.myiothome.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckLoginStatus {
    @Autowired
    HostHolder hostHolder;

    public boolean checkLogin(){
        if(hostHolder.getUser() != null){
            return true;
        }else{
            return false;
        }
    }
}

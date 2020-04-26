package com.myiothome.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

@Component
public class MyIotHomeUtils {
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    public static String md5(String password){
        if(password == null)
            return null;
        //产生md5字符串
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 阿里JSON库，将数据转换为json字符串
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);

        if(map != null){
            for(String key: map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }

        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

}

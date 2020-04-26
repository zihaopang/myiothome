package com.myiothome.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisUtils {
    private static final String SPLIT = ":";
    // Redis为实体类点赞的key名称的前缀
    private  static final String LIKE_ENTITY_PERFIX = "like:entity";
    //用户获得赞的数量前缀
    private static final String LIKE_USER_PERFIX = "like:user";
    //用户的粉丝前缀
    private static final String USER_FAN_PERFIX = "fans";
    //用户的关注前缀
    private static final String USER_FOCUS_PERFIX="focus";
    //网站独立用户前缀(UV)使用ip统计
    private static final String UNIQUE_VISITOR_PERFIX="uv";
    //网站每天活跃的用户数量
    private static final String DAILY_ACTIVE_USER_PERFIX = "dau";
    //帖子分数前缀
    private static final String POST_PERFIX = "socre";

    //得到对实体类点赞的key,格式：like:entity:entityType:entityId
    public static String getLikeEntityKey(int entityType,int entityId){
        String redisKey = LIKE_ENTITY_PERFIX + SPLIT + entityType + SPLIT + entityId;

        return redisKey;
    }

    //得到用户点赞数量的前缀
    public static String getLikeUserKey(int userId){
        return LIKE_USER_PERFIX+SPLIT+userId;
    }

    //某个用户关注的实体（可能是人，可能是帖子）
    public static String getUserFocusKey(int userId){
        return USER_FAN_PERFIX+SPLIT+userId;
    }

    //某个实体收到的关注（关注的可能是人，也可能是帖子之类的）
    public static String getEntityFansKey(int entityType,int entityId){
        return USER_FOCUS_PERFIX+SPLIT+entityType+SPLIT+entityId;
    }

    //网站独立ip用户（UV）的redis前缀
    public static String getUvKey(String date){
        return UNIQUE_VISITOR_PERFIX+SPLIT+date;
    }

    //一个时间范围内的(UV)的redis前缀
    public static String getUvKey(String start,String end){
        return UNIQUE_VISITOR_PERFIX+SPLIT+start+SPLIT+end;
    }

    //网站日活跃用户（DAU）的redis前缀
    public static String getDauKey(String date){
        return DAILY_ACTIVE_USER_PERFIX+SPLIT+date;
    }

    //一个时间范围内日活跃用户（DAU）的redis前缀
    public static String getDauKey(String start,String end){
        return DAILY_ACTIVE_USER_PERFIX+SPLIT+start+SPLIT+end;
    }

    //帖子分数redisKey
    public static String getSocreKey(){
        return POST_PERFIX+SPLIT+"score";
    }

}

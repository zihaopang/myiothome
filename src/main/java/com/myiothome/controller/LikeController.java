package com.myiothome.controller;

import com.myiothome.service.LikeService;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import com.myiothome.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements MyIotHomeConstent {
    @Autowired
    LikeService likeService;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int postUserId){
        likeService.like(entityType,entityId,postUserId);
        int likeStatus = likeService.isLiked(entityType,entityId);
        long likeNum = likeService.likeNum(entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeStatus",likeStatus);
        map.put("likeNum",likeNum);

        if(likeStatus == 1)//entityType：1是帖子，2是评论
            likeService.sendEvent(entityType,entityId,postUserId);

        //计算score
        if(entityType == POST){
            String scoreKey = RedisUtils.getSocreKey();
            redisTemplate.opsForSet().add(scoreKey,entityId);
        }

        return MyIotHomeUtils.getJSONString(0,null,map);
    }
}

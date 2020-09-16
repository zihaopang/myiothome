package com.myiothome.service;

import com.myiothome.entity.Event;
import com.myiothome.event.EventProducer;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LikeService implements MyIotHomeConstent {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;

    //点赞操作
    public void like(int entityType, int entityId, int postUserId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String likeEntityKey = RedisUtils.getLikeEntityKey(entityType, entityId);
                String likeUserKey = RedisUtils.getLikeUserKey(postUserId);//被赞用户的redis key

                int userId = hostHolder.getUser().getId();//点赞用户的Id

                boolean isMember = redisOperations.opsForSet().isMember(likeEntityKey, userId);

                redisOperations.multi();

                if (isMember) {
                    redisOperations.opsForSet().remove(likeEntityKey, userId);
                    redisOperations.opsForValue().decrement(likeUserKey);//减少点赞人数
                } else {
                    redisOperations.opsForSet().add(likeEntityKey, userId);
                    redisOperations.opsForValue().increment(likeUserKey);//增加用户点赞人数
                }

                return redisOperations.exec();

            }
        });
    }

    //是否喜欢
    public int isLiked(int entityType, int entityId) {
        String likeKey = RedisUtils.getLikeEntityKey(entityType, entityId);
        if (hostHolder.getUser() != null) {
            int userId = hostHolder.getUser().getId();

            return redisTemplate.opsForSet().isMember(likeKey, userId) ? 1 : 0;
        } else {
            return 0;
        }

    }

    //喜欢的人数
    public long likeNum(int entityType, int entityId) {
        String likeKey = RedisUtils.getLikeEntityKey(entityType, entityId);

        return redisTemplate.opsForSet().size(likeKey);
    }

    //获取某个实体获得的点赞量
    public int getLikedNum(int userId) {
        String likeUserKey = RedisUtils.getLikeUserKey(userId);
        Integer likeNum = (Integer) redisTemplate.opsForValue().get(likeUserKey);

        return likeNum == null ? 0 : likeNum.intValue();
    }

    //发送event给kafka
    public void sendEvent(int entityType, int entityId, int entityUserId) {
        Event event = new Event();
        int userId = hostHolder.getUser().getId();
        event.setTopic(LIKE).setEntityType(entityType).setEntityId(entityId).setEntityUserId(entityUserId)
                .setUserId(userId).setCreateTime(new Date()).setStatus(0);

        eventProducer.sendEvent(event);
    }
}

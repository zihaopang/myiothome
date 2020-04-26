package com.myiothome.service;

import com.myiothome.entity.User;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FocusService implements MyIotHomeConstent {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    //关注操作
    public void focus(int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                User user = hostHolder.getUser();

                String userFocusKey = RedisUtils.getUserFocusKey(user.getId());
                String entityFansKey = RedisUtils.getEntityFansKey(entityType,entityId);

                operations.multi();
                operations.opsForZSet().add(userFocusKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(entityFansKey,user.getId(),System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    //取消关注
    public void unFocus(int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                User user = hostHolder.getUser();

                String userFocusKey = RedisUtils.getUserFocusKey(user.getId());
                String entityFansKey = RedisUtils.getEntityFansKey(entityType,entityId);

                operations.multi();
                operations.opsForZSet().remove(userFocusKey,entityId);
                operations.opsForZSet().remove(entityFansKey,user.getId());

                return operations.exec();
            }
        });
    }

    //获取某人关注数量
    public long getUserfocusNum(int userId){
        String userFocusKey = RedisUtils.getUserFocusKey(userId);

        return redisTemplate.opsForZSet().size(userFocusKey);
    }

    //获取某实体的被关注数量
    public long getEntityFansNum(int entityType,int entityId){
        String entityFansKey = RedisUtils.getEntityFansKey(entityType,entityId);

        return redisTemplate.opsForZSet().size(entityFansKey);
    }

    //用户是否关注某个实体
    public int checkFocus(int userId,int entityId){
        String userFocusKey = RedisUtils.getUserFocusKey(userId);
        System.out.println("userFocusKey:"+userFocusKey);

        return redisTemplate.opsForZSet().score(userFocusKey,entityId) == null ? 0 :1;
    }

    //获取用户关注列表
    public List<Map<String,Object>> getFocusList(int userId,int limit,int offset){
        String userFocusKey = RedisUtils.getUserFocusKey(userId);

        List<Map<String,Object>> list = new ArrayList<>();

        Set<Integer> ids= redisTemplate.opsForZSet().range(userFocusKey,offset,limit+offset-1);

        for(Integer id : ids){
            Map<String,Object> map = new HashMap<>();
            map.put("user",userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(userFocusKey,id);
            map.put("time",new Date(score.longValue()));
            int checkFocus = checkFocus(userId,id);
            map.put("checkFocus",checkFocus);

            list.add(map);
        }

        return list;
    }

    //获取用户粉丝列表
    public List<Map<String,Object>> getFansList(int userId,int limit,int offset){
        String entityFansKey = RedisUtils.getEntityFansKey(USER,userId);

        List<Map<String,Object>> list = new ArrayList<>();

        Set<Integer> ids= redisTemplate.opsForZSet().range(entityFansKey,offset,limit+offset-1);

        for(Integer id : ids){
            Map<String,Object> map = new HashMap<>();
            map.put("user",userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(entityFansKey,id);
            map.put("time",new Date(score.longValue()));
            int checkFocus = checkFocus(id,userId);
            map.put("checkFocus",checkFocus);

            list.add(map);
        }

        return list;
    }
}

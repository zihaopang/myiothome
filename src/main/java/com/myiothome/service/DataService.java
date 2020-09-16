package com.myiothome.service;

import com.myiothome.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    public void setUv(String ip) {
        String redisKey = RedisUtils.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    public long getUvNum(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        List<String> dates = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            String redisKey = RedisUtils.getUvKey(df.format(calendar.getTime()));
            dates.add(redisKey);
            calendar.add(Calendar.DATE, 1);
        }

        String unionKey = RedisUtils.getUvKey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(unionKey, dates.toArray());

        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    public void setDau(int userId) {
        String redisKey = RedisUtils.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    public long getDauNum(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        List<byte[]> dates = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            String redisKey = RedisUtils.getDauKey(df.format(calendar.getTime()));
            dates.add(redisKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        String unionKey = RedisUtils.getDauKey(df.format(startDate), df.format(endDate));
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        unionKey.getBytes(), dates.toArray(new byte[0][0]));
                return connection.bitCount(unionKey.getBytes());
            }
        });
    }
}

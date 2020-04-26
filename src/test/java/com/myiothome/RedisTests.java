package com.myiothome;

//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class RedisTests {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 测试redis String:value 存储类型
     */
    @Test
    public void testString(){
        String redisKey="test:number";

        redisTemplate.opsForValue().set(redisKey,1);//设定数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));//读取数据
        redisTemplate.opsForValue().increment(redisKey);//加数据
        redisTemplate.opsForValue().decrement(redisKey);//减数据
    }

    /**
     * 测试redis String:Hash 存储类型
     */
    @Test
    public void testHash(){
        String redisKey = "test:hash";

        redisTemplate.opsForHash().put(redisKey,"username","pangzihao");
        redisTemplate.opsForHash().put(redisKey,"password","123");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"password"));

    }

    /**
     * 测试redis String:List 存储类型
     */
    @Test
    public void testList(){
        String redisKey = "test:list";

        redisTemplate.opsForList().leftPush(redisKey,"101");
        redisTemplate.opsForList().leftPush(redisKey,"102");
        redisTemplate.opsForList().leftPush(redisKey,"103");

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    /**
     * 测试redis String:Set 存储类型
     */
    @Test
    public void testSet(){
        String redisKey = "test:set";

        redisTemplate.opsForSet().add(redisKey,"张三","李四","王五","赵六");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    /**
     * 测试redis String:ShortedSet 存储类型
     */
    @Test
    public void testShortedSet(){
        String redisKey = "test:shortedSet";

        redisTemplate.opsForZSet().add(redisKey,"用户一",10);
        redisTemplate.opsForZSet().add(redisKey,"用户二",20);
        redisTemplate.opsForZSet().add(redisKey,"用户三",30);
        redisTemplate.opsForZSet().add(redisKey,"用户四",40);
        redisTemplate.opsForZSet().add(redisKey,"用户五",50);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));//统计个数
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"用户二"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"用户二"));
    }

    /**
     * redis 对key的相关操作
     */
    public void testKeys(){
        redisTemplate.delete("test:set");

        System.out.println(redisTemplate.hasKey("test:set"));

        redisTemplate.expire("test:list",10, TimeUnit.SECONDS);//设置过期时间
    }


    /**
     * 绑定redis
     */
    public void testBoundOperations(){
        String redisKey = "test:number";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();

        System.out.println(operations.get());
    }

    /**
     * 编程式事物
     */
    @Test
    public void testTransactional(){
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey="test:exec";

                redisOperations.multi();//事务开始

                redisOperations.opsForSet().add(redisKey,"张三");
                redisOperations.opsForSet().add(redisKey,"李四");
                redisOperations.opsForSet().add(redisKey,"王五");

                System.out.println(redisOperations.opsForSet().members(redisKey));

                return redisOperations.exec();//事务结束
            }
        });

        System.out.println(object);
    }

    //统计20万个独立数据总数
    @Test
    public void testHyperLoglog(){
        String redisKey = "test:hll:01";

        for (int i = 0; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        for (int i = 0; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,new Random().nextInt(100000));
        }

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    @Test
    public void testHyperLogUnion(){
        String redisKey2 = "test:hll:02";

        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:hll:03";

        for (int i = 5000; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:hll:04";

        for (int i = 15000; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        String redisKey5 = "test:hll:05";

        redisTemplate.opsForHyperLogLog().union(redisKey5,redisKey2,redisKey3,redisKey4,redisKey5);

        long size = redisTemplate.opsForHyperLogLog().size(redisKey5);
        System.out.println(size);
    }

    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";

        //赋值
        redisTemplate.opsForValue().setBit(redisKey,1,true);//设置第一位的布尔值
        redisTemplate.opsForValue().setBit(redisKey,3,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        //取值
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,7));

        //统计
        Object object = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(object);
    }

    //三组bool数据做or运算
    @Test
    public void testBitMapOr(){
        String redisKey1 = "test:bm:01";

        //赋值
        redisTemplate.opsForValue().setBit(redisKey1,1,true);//设置第一位的布尔值
        redisTemplate.opsForValue().setBit(redisKey1,3,true);
        redisTemplate.opsForValue().setBit(redisKey1,7,true);

        String redisKey2 = "test:bm:02";

        //赋值
        redisTemplate.opsForValue().setBit(redisKey2,2,true);//设置第一位的布尔值
        redisTemplate.opsForValue().setBit(redisKey2,4,true);
        redisTemplate.opsForValue().setBit(redisKey2,5,true);

        String redisKey3 = "test:bm:03";

        //赋值
        redisTemplate.opsForValue().setBit(redisKey3,6,true);//设置第一位的布尔值
        redisTemplate.opsForValue().setBit(redisKey3,8,true);
        redisTemplate.opsForValue().setBit(redisKey3,9,true);

        String orResult="test:or";
        //or运算，结果存储到orResult
        Object object = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        orResult.getBytes(),redisKey1.getBytes(),redisKey2.getBytes(),redisKey3.getBytes());
                return connection.bitCount(orResult.getBytes());
            }
        });

        System.out.println(object);
    }
}

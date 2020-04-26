package com.myiothome;

import com.myiothome.dao.UserMapper;
import com.myiothome.entity.Message;
import com.myiothome.entity.User;
import com.myiothome.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
class MyiothomeApplicationTests {
	@Autowired
	UserMapper userMapper;
	@Autowired
	MessageService messageService;

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

	@Test
	void contextLoads() {
//		User user = new User();
//		user.setHeaderUrl("aswefghghhj");
//		user.setStatus(0);
//		user.setEmail("123@qq.com");
//		user.setActivationCode("asdfgh");
//		user.setSalt("qwe");
//		user.setUsername("qsc");
//		user.setPassword("123");
//		user.setCreateTime(new Date());
//		user.setType(0);

		//userMapper.insertUser(user);

		//System.out.println(userMapper.selectUserById(3));
        //System.out.println(userMapper.selectUserByEmail(user.getEmail()));
        //System.out.println(userMapper.selectUserByName(user.getUsername()));

//        userMapper.updateUserEmail(3,"456@qq.com");
//        userMapper.updateUserName(3,"axcder");
//        userMapper.updateUserStatus(3,1);

		Message message = new Message();
		message.setFormId(23);
		message.setToId(22);
		message.setStatus(0);
		message.setContent("formId:23    toId:22 "+new Date());
		message.setCreateTime(new Date());

		messageService.addMessage(message);

		List<Message> list = messageService.findLatestMessage(999,0,100);
		System.out.println(list);

		System.out.println(messageService.findUnReadCount(888,999));

		System.out.println(messageService.findAllMessage(888,999,0,100));

		System.out.println(messageService.findAllMessageCount(0,999));
	}

}

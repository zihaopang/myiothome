package com.myiothome;

import com.myiothome.dao.EventMapper;
import com.myiothome.entity.Event;
import com.myiothome.service.EventService;
import org.apache.ibatis.annotations.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class EventTests {
    @Autowired
    EventService eventService;

    @Test
    public void testEventMapper(){

        Event event = new Event();

        event.setTopic("test").setEntityId(1).setEntityType(1)
                .setEntityUserId(2).setStatus(0).setCreateTime(new Date());


        eventService.insertEvent(event);

//        System.out.println(eventMapper.selectLatestEvent("test",2));
//        System.out.println(eventMapper.selectUnreadNum("test",2));
//        System.out.println(eventMapper.selectEvents("test",2,0,2));
    }
}

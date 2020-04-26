package com.myiothome.event;

import com.alibaba.fastjson.JSONObject;
import com.myiothome.dao.EventMapper;
import com.myiothome.entity.DiscussPost;
import com.myiothome.entity.Event;
import com.myiothome.service.DiscussPostService;
import com.myiothome.service.EventService;
import com.myiothome.service.SearchService;
import com.myiothome.util.MyIotHomeConstent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer implements MyIotHomeConstent {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    EventService eventService;
    @Autowired
    SearchService searchService;
    @Autowired
    DiscussPostService discussPostService;


    @KafkaListener(topics = {COMMENT,LIKE,FOCUS})
    public void handleEvent(ConsumerRecord record){
        if(record == null){
            logger.error("record为空");
            throw new IllegalArgumentException("record为空");
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);

        eventService.insertEvent(event);
    }

    @KafkaListener(topics = {SEARCH})
    public void handlerSearchEvent(ConsumerRecord record){
        if(record == null){
            logger.error("record为空");
            throw new IllegalArgumentException("record为空");
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);

        searchService.addPost(discussPostService.findDiscussPostByPostId(event.getEntityId()));
    }

    @KafkaListener(topics = {DELETE_POST})
    public void handlerDeletePostEvent(ConsumerRecord record){
        if(record == null){
            logger.error("record为空");
            throw new IllegalArgumentException("record为空");
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);

        searchService.deletePost(event.getEntityId());
    }
}

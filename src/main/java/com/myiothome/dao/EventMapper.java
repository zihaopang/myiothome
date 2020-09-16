package com.myiothome.dao;

import com.myiothome.entity.Event;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventMapper {
    Event selectLatestEvent(String topic, int entityUserId);//选出最新的数据

    int selectUnreadNum(String topic, int entityUserId);//选出未读数量

    int insertEvent(Event event);//增加事件

    int selectTopicNum(String topic, int entityUserId);//算出某人的某一事件未读数量

    List<Event> selectEvents(String topic, int entityUserId, int offset, int limit);//选择出消息

    int updateEventStatus(int id, int status);
}

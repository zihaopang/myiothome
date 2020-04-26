package com.myiothome.dao;

import com.myiothome.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    int insertMessage(Message message);

    List<Message> getLatestMessage(int toId, int offset, int limit);

    int unReadCount(int fromId,int toId);

    List<Message> getAllMessage(int fromId,int toId,int offset,int limit);

    int allMessageCount(int fromId,int toId);

    int updateStatus(List<Integer> ids,int status);
}

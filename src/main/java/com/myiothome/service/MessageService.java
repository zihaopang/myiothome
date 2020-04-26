package com.myiothome.service;

import com.myiothome.dao.MessageMapper;
import com.myiothome.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageMapper messageMapper;

    public int addMessage(Message message){
        return messageMapper.insertMessage(message);
    }

    public List<Message> findLatestMessage(int toId, int offset, int limit){
        return messageMapper.getLatestMessage(toId,offset,limit);
    }

    public int findUnReadCount(int fromId,int toId){
        return messageMapper.unReadCount(fromId,toId);
    }

    public List<Message> findAllMessage(int fromId,int toId,int offset,int limit){
        return messageMapper.getAllMessage(fromId,toId,offset,limit);
    }

    public int findAllMessageCount(int from,int toId){
        return messageMapper.allMessageCount(from,toId);
    }

    public int updateStatus(List<Integer> ids,int status){
        if(!ids.isEmpty()) {
            return messageMapper.updateStatus(ids, status);
        }

        return 0;
    }

}

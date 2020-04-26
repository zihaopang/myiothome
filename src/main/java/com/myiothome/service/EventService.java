package com.myiothome.service;

import com.myiothome.dao.EventMapper;
import com.myiothome.entity.Comment;
import com.myiothome.entity.Event;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService implements MyIotHomeConstent {
    @Autowired
    EventMapper eventMapper;
    @Autowired
    EventService eventService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    CommentService commentService;

    public Event findLatestEvent(String topic,int entityUserId){
        return eventMapper.selectLatestEvent(topic,entityUserId);
    }

    public int findUnreadNum(String topic,int entityUserId){
        return eventMapper.selectUnreadNum(topic,entityUserId);
    }

    public int insertEvent(Event event){
        return eventMapper.insertEvent(event);
    }

    public List<Event> findEvents(String topic,int entityUserId,int offset,int limit){
        return eventMapper.selectEvents(topic,entityUserId,offset,limit);
    }

    public int findTopicNum(String topic,int entityUserId){
        return eventMapper.selectTopicNum(topic,entityUserId);
    }
    public int updateEventStatus(int id,int status){
        return eventMapper.updateEventStatus(id,status);
    }
    public Map<String,Object> getNoticeMap(){
        Event likeEvent = eventService.findLatestEvent(LIKE,hostHolder.getUser().getId());
        Event commentEvent = eventService.findLatestEvent(COMMENT,hostHolder.getUser().getId());
        Event focusEvent = eventService.findLatestEvent(FOCUS,hostHolder.getUser().getId());

        Map<String,Object> result = new HashMap<>();

//        if(likeEvent==null && commentEvent==null && focusEvent==null){
//            result=null;
//            return null;
//        }

        int allUnreadNum = 0;

        result.put("likeEvent", likeEvent);
        if(likeEvent != null) {
            result.put("likeUser", userService.findUserById(likeEvent.getUserId()));
            result.put("likeUnreadNum", eventService.findUnreadNum(LIKE, hostHolder.getUser().getId()));
            allUnreadNum += eventService.findUnreadNum(LIKE, hostHolder.getUser().getId());
        }

        result.put("commentEvent", commentEvent);
        System.out.println(commentEvent);
        if(commentEvent != null) {
            result.put("commentUser", userService.findUserById(commentEvent.getUserId()));
            result.put("commentUnreadNum", eventService.findUnreadNum(COMMENT, hostHolder.getUser().getId()));
            allUnreadNum+=eventService.findUnreadNum(COMMENT, hostHolder.getUser().getId());
        }

        result.put("focusEvent", focusEvent);
        if(focusEvent != null) {
            result.put("focusUser", userService.findUserById(focusEvent.getUserId()));
            result.put("focusUnreadNum", eventService.findUnreadNum(FOCUS, hostHolder.getUser().getId()));
            allUnreadNum+=eventService.findUnreadNum(FOCUS, hostHolder.getUser().getId());
        }

        result.put("allUnreadNum",allUnreadNum);

        return result;
    }

    public List<Map<String,Object>> getNoticeList(String topic,List<Event> list){
        List<Map<String,Object>> result = new ArrayList<>();

        int postId = 0;

        if(list != null){
            for(Event event: list){
                Map<String,Object> map = new HashMap<>();
                map.put("event",event);
                this.updateEventStatus(event.getId(),1);//设置已读
                if(topic.equals(LIKE) || topic.equals(COMMENT)){
                    if(event.getEntityType() == 1){//评论或者点赞的是帖子
                        postId = event.getEntityId();
                    }else{//评论或者点赞的是评论
                        Comment comment = commentService.findCommentById(event.getEntityId());
                        postId = comment.getEntityId();
                    }

                    map.put("discussPost",discussPostService.findDiscussPostByPostId(postId));
                }

                map.put("user",userService.findUserById(event.getUserId()));

                result.add(map);
            }
        }

        return result;
    }
}

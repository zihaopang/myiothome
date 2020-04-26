package com.myiothome.controller;

import com.myiothome.entity.Comment;
import com.myiothome.entity.DiscussPost;
import com.myiothome.entity.Event;
import com.myiothome.event.EventProducer;
import com.myiothome.service.CommentService;
import com.myiothome.service.DiscussPostService;
import com.myiothome.service.UserService;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path="/comment")
public class CommentController implements MyIotHomeConstent {

    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(path="/add/{postId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId,Comment comment, Model model){

        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());

        if(!StringUtils.isBlank(comment.getContent())){
            commentService.addComment(comment);
        }

        if(comment.getEntityType() == REPLY_FIRST){//评论的是帖子
            DiscussPost discussPost = discussPostService.findDiscussPostByPostId(postId);
            int commentCount = discussPost.getCommentCount();
            discussPostService.updateCommentCount(postId,commentCount+1);
            Event event = new Event();
            event.setTopic(COMMENT).setEntityType(REPLY_FIRST).setEntityId(discussPost.getId())
                    .setEntityUserId(discussPost.getUserId()).setStatus(0).setCreateTime(new Date())
                    .setUserId(hostHolder.getUser().getId());
            eventProducer.sendEvent(event);

            //计算分数
            String scoreKey = RedisUtils.getSocreKey();
            redisTemplate.opsForSet().add(scoreKey,postId);
        }else {
            DiscussPost discussPost = discussPostService.findDiscussPostByPostId(postId);
            Event event = new Event();
            System.out.println(comment);
            event.setTopic(COMMENT).setEntityType(REPLY_SENCOD).setEntityId(comment.getId())
                    .setEntityUserId(comment.getUserId()).setStatus(0).setCreateTime(new Date())
                    .setUserId(hostHolder.getUser().getId());
            System.out.println(event);
            eventProducer.sendEvent(event);
        }

        //往elasticsearch服务器插入新的post数据
        Event event = new Event();
        event.setTopic(SEARCH).setEntityType(POST).setEntityId(postId);
        eventProducer.sendEvent(event);

        return "redirect:/discuss/detail/"+postId;
    }

}

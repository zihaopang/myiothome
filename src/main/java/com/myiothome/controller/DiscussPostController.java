package com.myiothome.controller;

import com.myiothome.entity.*;
import com.myiothome.event.EventProducer;
import com.myiothome.service.*;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import com.myiothome.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements MyIotHomeConstent {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    SearchService searchService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
         User user = hostHolder.getUser();
         System.out.println(title+content);
         if(user == null){
             return MyIotHomeUtils.getJSONString(403,"您还没有登录!");
         }

         if(title == null || StringUtils.isBlank(title)){
             return MyIotHomeUtils.getJSONString(402,"发布失败，标题为空!");
         }

        if(content == null || StringUtils.isBlank(content)){
            return MyIotHomeUtils.getJSONString(401,"发布失败，内容为空!");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        String scoreKey = RedisUtils.getSocreKey();
        redisTemplate.opsForSet().add(scoreKey,discussPost.getId());

        //往elasticsearch服务器插入新的post数据
        Event event = new Event();
        event.setTopic(SEARCH).setEntityType(POST).setEntityId(discussPost.getId())
                .setEntityUserId(discussPost.getUserId());

        eventProducer.sendEvent(event);

        return MyIotHomeUtils.getJSONString(0,"发布成功！");
    }

    @RequestMapping(path = "/detail/{postId}",method = RequestMethod.GET)
    public String getDiscussPage(@PathVariable("postId") int postId, Model model, Page page){
        int userId = discussPostService.findUserIdByPostId(postId);
        User user = userService.findUserById(userId);
        DiscussPost discussPost = discussPostService.findDiscussPostByPostId(postId);

        page.setPostsNum(commentService.findCommentsNumByEntityId(postId));
        page.setLimit(3);
        page.setPath("/site/post-detail");

        List<Comment> firstCommentList = commentService.findCommentsByEntityId(REPLY_FIRST,postId,page.getLimit(),page.getOffset());
        model.addAttribute("user",user);
        model.addAttribute("discussPost",discussPost);
        model.addAttribute("discussPostLikeNum",likeService.likeNum(1,discussPost.getId()));
        model.addAttribute("postLikeStatus",likeService.isLiked(1,discussPost.getId()));
        List<Map<String,Object>> firstList = new ArrayList<>();

        /**
         * 下面一段比较复杂。
         * 首先，一个帖子下面有很多一级评论，这些一级评论的下面也会有很多二级评论。
         * 同时，这些二级评论也会有互相评论的情况。
         * 1.首先，firstList装载着一个评论的全部内容，包括一级评论以及评论一级评论的二级评论。
         * 2.先把所有一级评论的内容全部取得，包括评论的实体：Comment以及评论者：firstCommentUser
         * 3.然后，再将评论这些一级评论的评论找出来，也就是二级评论
         * 4.二级评论也包括评论实体：Comment 以及 secondCommentUser，但是还包含这个评论是否是评论二级评论的评论：
         * 5.User targetUser = com.getTargetId() == 0 ? null : userService.findUserById(com.getTargetId());
         * 6.将这个信息也装载到二级评论的List:secondList中去
         * 7.最后返回完整的一级评论
         */

        if(firstCommentList != null) {
            for (Comment comment : firstCommentList) {
                Map<String,Object> firstObject = new HashMap<>();

                //一级评论的作者和评论内容
                User firstCommentUser = userService.findUserById(comment.getUserId());
                firstObject.put("user", firstCommentUser);
                firstObject.put("comment", comment);
                firstObject.put("likeNum",likeService.likeNum(2,comment.getId()));
                firstObject.put("likeStatus",likeService.isLiked(2,comment.getId()));
                //寻找出评论该一级评论的二级评论
                List<Comment> secondCommentList = commentService.findCommentsByEntityId(REPLY_SENCOD,comment.getId(),Integer.MAX_VALUE,0);
                List<Map<String,Object>> secondList = new ArrayList<>();

                for(Comment com : secondCommentList){
                    Map<String,Object> secondObject = new HashMap<>();

                    User secondCommentUser = userService.findUserById(com.getUserId());
                    secondObject.put("user",secondCommentUser);
                    secondObject.put("comment",com);
                    secondObject.put("likeNum",likeService.likeNum(2,com.getId()));
                    secondObject.put("likeStatus",likeService.isLiked(2,com.getId()));
                    User targetUser = com.getTargetId() == 0 ? null : userService.findUserById(com.getTargetId());
                    secondObject.put("target",targetUser);
                    secondList.add(secondObject);
                }

                firstObject.put("secondList",secondList);

                firstList.add(firstObject);

                System.out.println("---------------------------------");
            }

        }

        model.addAttribute("firstComments",firstList);

        return "/site/post-detail";
    }

    //置顶，异步请求
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setPostTop(int postId){
        discussPostService.updatePostType(postId,1);

        //往elasticsearch服务器插入新的post数据
        Event event = new Event();
        event.setTopic(SEARCH).setEntityType(POST).setEntityId(postId)
                .setEntityUserId(postId);

        eventProducer.sendEvent(event);
        //计算score
        String scoreKey = RedisUtils.getSocreKey();
        redisTemplate.opsForSet().add(scoreKey,postId);

        return MyIotHomeUtils.getJSONString(0);//置顶成功
    }

    //帖子加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setPostWonderful(int postId){
        discussPostService.updatePostStatus(postId,1);

        //往elasticsearch服务器插入新的post数据
        Event event = new Event();
        event.setTopic(SEARCH).setEntityType(POST).setEntityId(postId)
                .setEntityUserId(postId);

        eventProducer.sendEvent(event);

        return MyIotHomeUtils.getJSONString(0);//加精成功
    }

    //删除帖子
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deletePost(int postId){
        discussPostService.updatePostStatus(postId,2);

        //往elasticsearch服务器插入新的post数据
        Event event = new Event();
        event.setTopic(DELETE_POST).setEntityType(POST).setEntityId(postId)
                .setEntityUserId(postId);

        eventProducer.sendEvent(event);

        return MyIotHomeUtils.getJSONString(0);//删除成功
    }
}

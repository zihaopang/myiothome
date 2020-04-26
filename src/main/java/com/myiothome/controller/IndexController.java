package com.myiothome.controller;

import com.myiothome.annotation.LoginRequired;
import com.myiothome.entity.DiscussPost;
import com.myiothome.entity.Page;
import com.myiothome.entity.User;
import com.myiothome.service.*;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController implements MyIotHomeConstent {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    LikeService likeService;
    @Autowired
    FocusService focusService;

    @RequestMapping(path = "/",method = RequestMethod.GET)
    public String root(){
        return "forward:/index";
    }

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String homePage(Model model, Page page,@RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        page.setPostsNum(discussPostService.findPostsNum());
        page.setLimit(3);
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> posts = discussPostService.findDiscussPosts(page.getLimit(),page.getOffset(),orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();


        for(DiscussPost discussPost : posts){
            Map<String,Object> map = new HashMap<>();
            map.put("post",discussPost);
            map.put("user",userService.findUserById(discussPost.getUserId()));
            map.put("likeCount",likeService.likeNum(1,discussPost.getId()));
            discussPosts.add(map);
        }

        model.addAttribute("posts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getError(){
        return "/error/500";
    }

    @LoginRequired
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getUserProfile(@PathVariable("userId") int userId, Model model){
        User user = hostHolder.getUser();

        int likeNum = likeService.getLikedNum(userId);
        long focusNum = focusService.getUserfocusNum(userId);
        long entityFansNum = focusService.getEntityFansNum(USER,userId);

        int focusCheck = user!=null?focusService.checkFocus(user.getId(),userId):0;

        model.addAttribute("likeNum",likeNum);
        model.addAttribute("user",userService.findUserById(userId));
        model.addAttribute("focusNum",focusNum);
        model.addAttribute("entityFansNum",entityFansNum);
        model.addAttribute("checkFocus",focusCheck);
        return "/site/profile";
    }

    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/site/denied";
    }
}

package com.myiothome.controller;

import com.myiothome.entity.Comment;
import com.myiothome.entity.Event;
import com.myiothome.entity.Page;
import com.myiothome.entity.User;
import com.myiothome.service.CommentService;
import com.myiothome.service.DiscussPostService;
import com.myiothome.service.EventService;
import com.myiothome.service.UserService;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.standard.expression.GenericTokenExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EventController implements MyIotHomeConstent {
    @Autowired
    EventService eventService;

    @RequestMapping(path = "/notice",method = RequestMethod.GET)
    public String getNotices(Model model){
        Map<String,Object> result = eventService.getNoticeMap();

        model.addAttribute("result",result);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/{topic}/{entityUserId}",method = RequestMethod.GET)
    public String getNoticeList(@PathVariable("topic") String topic, @PathVariable("entityUserId") int entityUserId, Model model, Page page){

        page.setPath("/notice/"+topic+"/"+entityUserId);
        page.setPostsNum(eventService.findTopicNum(topic,entityUserId));
        page.setLimit(3);
        List<Event> list = eventService.findEvents(topic,entityUserId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> result = eventService.getNoticeList(topic,list);

        model.addAttribute("result",result);

        return "/site/notice-detail";
    }
}

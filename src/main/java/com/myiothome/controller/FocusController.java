package com.myiothome.controller;

import com.myiothome.entity.Event;
import com.myiothome.entity.Page;
import com.myiothome.event.EventProducer;
import com.myiothome.service.FocusService;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FocusController implements MyIotHomeConstent{
    @Autowired
    FocusService focusService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/focus",method = RequestMethod.POST)
    @ResponseBody
    public String focus(int entityType,int entityId){

        focusService.focus(entityType,entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("fansNum",focusService.getEntityFansNum(entityType,entityId));
        Event event = new Event();
        event.setTopic(FOCUS).setUserId(hostHolder.getUser().getId()).setEntityType(entityType)
                .setEntityId(entityId).setEntityUserId(entityId).setStatus(0).setCreateTime(new Date());
        eventProducer.sendEvent(event);

        return MyIotHomeUtils.getJSONString(0,"关注成功！",map);
    }

    @RequestMapping(path = "/unfocus",method = RequestMethod.POST)
    @ResponseBody
    public String unfocus(int entityType,int entityId){
        focusService.unFocus(entityType,entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("fansNum",focusService.getEntityFansNum(entityType,entityId));
        return MyIotHomeUtils.getJSONString(0,"已取消关注！",map);
    }
    //获取用户关注列表
    @RequestMapping(path = "/focusList/{userId}",method = RequestMethod.GET)
    public String getFocusList(@PathVariable("userId") int userId, Model model, Page page){
        page.setPath("/focusList/"+userId);
        page.setPostsNum((int)focusService.getUserfocusNum(userId));
        page.setLimit(3);
        List<Map<String,Object>> list = focusService.getFocusList(userId,page.getLimit(),page.getOffset());

        model.addAttribute("user",hostHolder.getUser());
        model.addAttribute("list",list);

        return "/site/focus";
    }
    //获取粉丝关注列表
    @RequestMapping(path = "/fansList/{userId}",method = RequestMethod.GET)
    public String getFansList(@PathVariable("userId") int userId, Model model, Page page){
        page.setPath("/fansList/"+userId);
        page.setPostsNum((int)focusService.getEntityFansNum(USER,userId));
        page.setLimit(3);
        List<Map<String,Object>> list = focusService.getFansList(userId,page.getLimit(),page.getOffset());

        model.addAttribute("user",hostHolder.getUser());
        model.addAttribute("list",list);

        return "/site/fans";
    }
}

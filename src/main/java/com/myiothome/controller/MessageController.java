package com.myiothome.controller;

import com.myiothome.entity.Message;
import com.myiothome.entity.Page;
import com.myiothome.entity.User;
import com.myiothome.service.MessageService;
import com.myiothome.service.UserService;
import com.myiothome.util.HostHolder;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.MyIotHomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/message")
public class MessageController implements MyIotHomeConstent {
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @RequestMapping(path = "/list",method = RequestMethod.GET)
    public String getMessageList(Model model, Page page){
        page.setPath("/message/list");
        page.setLimit(3);
        page.setPostsNum(messageService.findAllMessageCount(0,hostHolder.getUser().getId()));
        int allNum = messageService.findUnReadCount(0,hostHolder.getUser().getId());

        List<Message> messageList = messageService.findLatestMessage(hostHolder.getUser().getId(),
                page.getOffset(),page.getLimit());

        List<Map<String,Object>> listInfo = new ArrayList<>();

        for(Message message : messageList){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(message.getFormId());
            map.put("user",user);
            map.put("message",message);
            int unreadNum = messageService.findUnReadCount(message.getFormId(),hostHolder.getUser().getId());
            map.put("unReadNum",unreadNum);

            listInfo.add(map);
        }

        model.addAttribute("lateList",listInfo);
        model.addAttribute("allNum",allNum);
        return "/site/message";
    }

    @RequestMapping(path = "/list/{userId}",method = RequestMethod.GET)
    public String getMessageDetail(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        page.setPath("/message/list/"+userId);
        page.setLimit(3);
        page.setPostsNum(messageService.findAllMessageCount(userId,hostHolder.getUser().getId()));

        List<Integer> ids = new ArrayList<>();

        List<Message> messageList = messageService.findAllMessage(userId,hostHolder.getUser().getId(),
                                                                  page.getOffset(),page.getLimit());
        for(Message message : messageList){
            ids.add(message.getId());
        }
        System.out.println(ids);
        messageService.updateStatus(ids,READ);

        model.addAttribute("messageList",messageList);

        return "/site/message-detail";
    }

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(String person,String content){
        User user = userService.findUserByName(person);

        if(user == null){
            return MyIotHomeUtils.getJSONString(403,"没有该接收者！");
        }

        Message message = new Message();
        message.setFormId(hostHolder.getUser().getId());
        message.setToId(user.getId());
        message.setStatus(UNREAD);
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return MyIotHomeUtils.getJSONString(0,"发送成功！");
    }
}

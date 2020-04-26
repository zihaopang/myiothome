package com.myiothome.controller;

import com.myiothome.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    DataService dataService;
    @RequestMapping(path="/data",method = RequestMethod.GET)
    public String getDataPage(){
        return "/site/admin/data";
    }

    @RequestMapping(path="/data/uv",method = RequestMethod.GET)
    public String getUvPage(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                            Model model){
        if(start != null && end != null){
            long uvNum = dataService.getUvNum(start,end);
            System.out.println("ssss"+uvNum);

            model.addAttribute("uvNum",uvNum);
            model.addAttribute("uvStart",start);
            model.addAttribute("uvEnd",end);
        }

        return "/site/admin/data";
    }

    @RequestMapping(path="/data/dau",method = RequestMethod.GET)
    public String getDauPage(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                            @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                            Model model){
        if(start != null && end != null) {
            long dauNum = dataService.getDauNum(start,end);

            model.addAttribute("dauNum",dauNum);
            model.addAttribute("dauStart",start);
            model.addAttribute("dauEnd",end);
        }

        return "/site/admin/data";
    }


}

package com.myiothome.controller;

import com.myiothome.entity.DiscussPost;
import com.myiothome.entity.User;
import com.myiothome.service.LikeService;
import com.myiothome.service.SearchService;
import com.myiothome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String getSearchPage(Model model, String keyWords, com.myiothome.entity.Page page){
        page.setPath("/site/search");
        page.setLimit(3);
        Page<DiscussPost> searchResult = searchService.search(keyWords,page.getCurrent()-1,page.getLimit());
        List<Map<String,Object>> result = new ArrayList<>();

        if(searchResult != null){
            page.setPostsNum((int)searchResult.getTotalElements());

            for(DiscussPost post : searchResult){
                Map<String,Object> map = new HashMap<>();
                User user = userService.findUserById(post.getUserId());
                int likeCount = (int)likeService.likeNum(1,post.getId());
                map.put("user",user);
                map.put("post",post);
                map.put("likeCount",likeCount);

                result.add(map);
            }
        }

        model.addAttribute("result",result);

        return "/site/search";
    }
}

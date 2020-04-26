package com.myiothome;

import com.myiothome.entity.DiscussPost;
import com.myiothome.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class PressTests {

    @Autowired
    DiscussPostService discussPostService;

    @Test
    public void insertPosts()
    {
        for (int i = 0; i < 200000; i++) {
            DiscussPost post = new DiscussPost();
            post.setTitle("hello");
            post.setContent("这次意大利疫情" +
                    "，欧盟和西方国家的真实面目被彻底揭穿，" +
                    "欧盟成员国在意大利最需要援助的时刻“集体隐身”，" +
                    "第一时间伸出援手的是中国和俄罗斯，" +
                    "其中和意大利交情不错的俄罗斯更是直接出动15架" +
                    "伊尔-76MD战略运输机千里驰援。中俄鼎力相助意大利在国际上" +
                    "引起了热烈反响，这时候有些欧洲国家就慌了，开始说一些阴" +
                    "阳怪气的风凉话");
            post.setCreateTime(new Date());
            post.setUserId(123);
            post.setScore(Math.random());

            discussPostService.addDiscussPost(post);
        }
    }
}

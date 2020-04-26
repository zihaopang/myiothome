package com.myiothome.quartz;

import com.myiothome.entity.DiscussPost;
import com.myiothome.entity.Event;
import com.myiothome.event.EventProducer;
import com.myiothome.service.DiscussPostService;
import com.myiothome.service.LikeService;
import com.myiothome.service.SearchService;
import com.myiothome.util.MyIotHomeConstent;
import com.myiothome.util.RedisUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.xml.crypto.Data;
import java.util.Date;

public class PostScoreJob implements Job , MyIotHomeConstent {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer eventProducer;

    /**
     * 分数计算公式：
     * log(精华分+点赞数*2+评论数*5)/(10+发布时间长短)
     */

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String scoreKey = RedisUtils.getSocreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(scoreKey);//得到一个集合

        if(operations.size() != 0){
            this.refresh((Integer) operations.pop());//每次弹出一个postId进行计算
        }
    }

    private void refresh(int postId){
        DiscussPost discussPost = discussPostService.findDiscussPostByPostId(postId);
        Date createTime = discussPost.getCreateTime();
        Date nowTime = new Date();
        //是否为精华
        int wonderful = discussPost.getType()==1?1:0;
        //点赞数
        long likeNum = likeService.likeNum(POST,discussPost.getId());
        //评论数
        int commentNum = discussPost.getCommentCount();

        double score = Math.log(wonderful+likeNum*2+commentNum*5)/
                (10+(nowTime.getTime()-discussPost.getCreateTime().getTime())/(1000*60*60*24));

        discussPostService.updatePostScore(discussPost.getId(),score);
        //同步到elasticSearch数据库
        Event event = new Event().setEntityId(discussPost.getId())
                                .setTopic(SEARCH);
        eventProducer.sendEvent(event);
    }
}

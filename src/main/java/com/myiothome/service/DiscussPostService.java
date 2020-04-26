package com.myiothome.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.myiothome.dao.DiscussPostMapper;
import com.myiothome.entity.DiscussPost;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.acl.LastOwnerException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Value("${caffine.posts.max-size}")
    int caffineSize;

    @Value("${caffine.posts.expire-seconds}")
    int caffineExpireSeconds;

    //帖子列表的缓存
    LoadingCache<String,List<DiscussPost>> postsCache;

    //帖子总数的缓存
    LoadingCache<Integer,Integer> postNumCache;

    @PostConstruct
    public void init(){
        //缓存帖子列表
        postsCache = Caffeine.newBuilder()
                .maximumSize(caffineSize)
                .expireAfterWrite(caffineExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0)
                            throw new IllegalArgumentException("参数错误！");
                        String[] params = key.split(":");

                        if(params==null || params.length != 2){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        logger.info("loading posts from cache...");

                        return discussPostMapper.selectDiscussPosts(limit,offset,1);//这里用Mapper
                    }
                });
        //缓存帖子总数
        postNumCache = Caffeine.newBuilder()
                .maximumSize(caffineSize)
                .expireAfterWrite(caffineExpireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer num) throws Exception {
                        return discussPostMapper.selectPostsNum();
                    }
                });
    }

    public int addDiscussPost(DiscussPost discussPost){
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public List<DiscussPost> findDiscussPosts(int limit,int offset,int orderMode){
        if(orderMode == 1)//是热门帖子，则进行缓存
        {
            return postsCache.get(offset+":"+limit);
        }
        logger.info("loading posts from DB...");
        return discussPostMapper.selectDiscussPosts(limit,offset,orderMode);
    }

    public int findPostsNum(){
        return postNumCache.get(0);
        //return discussPostMapper.selectPostsNum();
    }

    public int findUserIdByPostId(int postId){
        return discussPostMapper.selectUserIdByPostId(postId);
    }

    public DiscussPost findDiscussPostByPostId(int postId){
        return discussPostMapper.selectDiscussPostByPostId(postId);
    }

    public int updateCommentCount(int postId,int count){
        return discussPostMapper.updateCommentCount(postId,count);
    }

    public int updatePostStatus(int id,int status){
        return discussPostMapper.updatePostStatus(id,status);
    }

    public int updatePostType(int id,int type){
        return discussPostMapper.updatePostType(id,type);
    }

    public int updatePostScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}

package com.myiothome.dao;

import com.myiothome.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    int insertDiscussPost(DiscussPost discussPost);

    List<DiscussPost> selectDiscussPosts(int limit,int offset,int orderMode);

    int selectPostsNum();

    int selectUserIdByPostId(int postId);

    DiscussPost selectDiscussPostByPostId(int postId);

    int updateCommentCount(int postId,int count);

    int updatePostStatus(int id,int status);

    int updatePostType(int id,int type);

    int updateScore(int id,double score);
}

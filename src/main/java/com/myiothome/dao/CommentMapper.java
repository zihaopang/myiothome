package com.myiothome.dao;

import com.myiothome.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntityId(int entityType,int entityId,int limit,int offset);

    int insertComment(Comment comment);

    int selectCommentsNumByEntityId(int entity);

    Comment selectCommentById(int id);

    Comment selectLatestComment();
}

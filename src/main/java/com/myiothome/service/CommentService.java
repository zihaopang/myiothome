package com.myiothome.service;

import com.myiothome.dao.CommentMapper;
import com.myiothome.entity.Comment;
import com.myiothome.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;

    public List<Comment> findCommentsByEntityId(int entityType,int entityId,int limit,int offset){
        return commentMapper.selectCommentsByEntityId(entityType,entityId,limit,offset);
    }

    public int addComment(Comment comment){
        return commentMapper.insertComment(comment);
    }

    public int findCommentsNumByEntityId(int entityId){
        return commentMapper.selectCommentsNumByEntityId(entityId);
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

    public Comment findLatestComment(){
        return commentMapper.selectLatestComment();
    }
}

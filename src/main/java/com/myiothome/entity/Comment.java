package com.myiothome.entity;

import java.util.Date;

public class Comment {
    private int id;
    private int userId;
    private int entityType;//评论的类型，可能是帖子，可能是课程等等
    private int entityId;//评论的实体ID
    private int targetId;//评论的评论的目标ID
    private String content;
    private int status;
    private Date createTime;

    public int getTargetId() {
        return targetId;
    }

    public Comment setTargetId(int targetId) {
        this.targetId = targetId;
        return this;
    }

    public int getId() {
        return id;
    }

    public Comment setId(int id) {
        this.id = id;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Comment setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Comment setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Comment setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Comment setContent(String content) {
        this.content = content;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Comment setStatus(int status) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Comment setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}

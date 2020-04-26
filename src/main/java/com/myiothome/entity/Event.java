package com.myiothome.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    int id;
    String topic;
    int userId;//做出点赞等操作的人
    int entityType;
    int entityId;
    int entityUserId;//被点赞的人
    int status;//0未读，1已读
    Date createTime;

    public int getId() {
        return id;
    }

    public Event setId(int id) {
        this.id = id;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Event setStatus(int status) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Event setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}

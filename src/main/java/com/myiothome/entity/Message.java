package com.myiothome.entity;

import java.util.Date;

public class Message {
    int id;
    int fromId;
    int toId;
    int status;//'0-未读;1-已读;2-删除;
    String content;
    Date createTime;

    public int getId() {
        return id;
    }

    public Message setId(int id) {
        this.id = id;
        return this;
    }

    public int getFormId() {
        return fromId;
    }

    public Message setFormId(int formId) {
        this.fromId = formId;
        return this;
    }

    public int getToId() {
        return toId;
    }

    public Message setToId(int toId) {
        this.toId = toId;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Message setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String context) {
        this.content = context;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Message setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", formId=" + fromId +
                ", toId=" + toId +
                ", status=" + status +
                ", context='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

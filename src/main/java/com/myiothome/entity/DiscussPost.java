package com.myiothome.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

//elasticsearch的设置，索引名称，_doc为类型名，是一个占位符，shards：分片，replicas：备份
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)
public class DiscussPost {
    @Id
    int id;

    @Field(type = FieldType.Integer)
    int userId;

    /**
     * title为需要被搜索的字段
     * nalyzer = "ik_max_word"：这事这个参数，比如：互联网校招，它会将
     * 这五个字拆分成尽可能多的单词：互联、校招、联网、互联网等等
     * searchAnalyzer = "ik_smart"：这是搜索策略，比如互联网校招，他可能只搜索互联网、校招
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    String content;

    @Field(type = FieldType.Integer)
    int type;//0为普通,1为置顶

    @Field(type = FieldType.Integer)
    int status;//'0-正常; 1-精华; 2-删除;',

    @Field(type = FieldType.Date)
    Date createTime;

    @Field(type = FieldType.Integer)
    int commentCount;

    @Field(type = FieldType.Double)
    double score;

    public int getId() {
        return id;
    }

    public DiscussPost setId(int id) {
        this.id = id;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public DiscussPost setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public DiscussPost setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public DiscussPost setContent(String content) {
        this.content = content;
        return this;
    }

    public int getType() {
        return type;
    }

    public DiscussPost setType(int type) {
        this.type = type;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public DiscussPost setStatus(int status) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public DiscussPost setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public DiscussPost setCommentCount(int commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public double getScore() {
        return score;
    }

    public DiscussPost setScore(double score) {
        this.score = score;
        return this;
    }

    @Override
    public String toString() {
        return "DiscussPost{" +
                "userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", commentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}

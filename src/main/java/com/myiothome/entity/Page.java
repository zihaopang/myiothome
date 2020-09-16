package com.myiothome.entity;

/*
分页类
 */
public class Page {
    String path;
    private int current = 1;
    private int postsNum;
    private int limit;
    private int pagesNum;
    private int offset;
    private int from;
    private int to;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset() {
        return (current - 1) * limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) {
            this.current = current;
        }
    }

    public int getPostsNum() {
        return postsNum;
    }

    public void setPostsNum(int postsNum) {
        this.postsNum = postsNum;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit > 0 && limit < 30) {
            this.limit = limit;
        }
    }

    public int getPagesNum() {
        if (postsNum % limit == 0)
            return postsNum / limit;
        else
            return postsNum / limit + 1;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }

    public int getFrom() {
        int num = current - 2;
        return num < 1 ? 1 : num;
    }

    public void setFrom(int from) {

        this.from = from;
    }

    public int getTo() {
        int num = current + 2;
        int pageNum = getPagesNum();
        if (pageNum == 0)
            return 1;
        return num > pageNum ? pageNum : num;
    }

    public void setTo(int to) {

        this.to = to;
    }
}

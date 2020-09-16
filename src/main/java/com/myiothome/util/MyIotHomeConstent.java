package com.myiothome.util;

public interface MyIotHomeConstent {
    /*
    用户状态代码代码
     */
    int NO_ACTIVATE = 0;
    int ACTIVATED = 1;
    int BLACK_SHEET = 2;

    /*
    cookie失效时间
     */
    int ONE_MONTH = 3600 * 24 * 100;
    int ONE_DAY = 3600 * 24;

    /*
    回复贴的类型
     */
    int REPLY_FIRST = 1;//一级回复
    int REPLY_SENCOD = 2;//二级回复，即回复别人的

    /*
    信息状态
     */
    int UNREAD = 0;
    int READ = 1;
    int DELETE = 2;

    /*
    关注的实体类型
     */
    int POST = 1;
    int USER = 3;

    /*
    主题类型
    */
    String COMMENT = "comment";
    String LIKE = "like";
    String FOCUS = "focus";
    String SEARCH = "search";
    String DELETE_POST = "deletepost";

    /*
    登陆权限类型
     */
    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODEARTOR = "moderator";

}

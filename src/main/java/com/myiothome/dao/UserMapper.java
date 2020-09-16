package com.myiothome.dao;

import com.myiothome.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserById(int id);

    User selectUserByName(String userName);

    User selectUserByEmail(String email);

    int insertUser(User user);

    int updateUserStatus(int id, int status);

    int updateUserName(int id, String userName);

    int updateUserEmail(int id, String email);

    int updateUserHeaderUrl(int id, String headerUrl);
}

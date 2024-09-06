package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("select * from user where id = #{id}")
    User getUserById(int id);

    @Select("select * from user where username = #{username} and password = #{password}")
    User getUserByNameAndPwd(@Param("username") String username, @Param("password")String password);
}

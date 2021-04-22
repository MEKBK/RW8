package com.kbk.dao;

import com.kbk.model.User;

import java.util.List;

/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/03/28 10:14
 * @Version 1.0
 *
 */
public interface UserMapper {

    /**
     * 添加用户
     * @param user
     * @return
     */
    int insertSelective(User user);

    /**
     * 查询用户名
     * @param username
     * @return
     */
    User selectUserName(String username);


    /**
     * 上传头像
     * @param user
     * @return
     */
    boolean upHeadPortrait(User user);

    /**
     * 根据主键ID查找人物
     * @param id
     * @return
     */
    User selectByPrimaryKey(Integer id);

}
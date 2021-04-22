package com.kbk.service.Impl;

import com.kbk.dao.UserMapper;
import com.kbk.model.User;
import com.kbk.service.UserService;
import com.kbk.util.ImgException;
import com.kbk.util.OSSCOSUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    OSSCOSUtil osscosUtil;

    @Override
    public int insertSelective(User user){
        return userMapper.insertSelective(user);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User selectUserName(String name){
        ValueOperations<String, Object> vos = redisTemplate.opsForValue();

        User user = null;

        if ("".equals(vos.get(name))){
            logger.info("\n从缓存返回空值");
            return user;
        }


        try {
            if (vos.get(name) != null) {

                user = (User) vos.get(name);
                logger.info("\n" + "从缓存中查询单条数据" + user);

            } else {


                user = userMapper.selectUserName(name);
                logger.info("\n" + "从数据库中查询单条数据" + user);


                if (user != null) {
                    vos.set(name, user);
                    logger.info("\n" + "新增单条缓存数据:" + vos.get(name));
                } else {
                    vos.set(name,"", 60, TimeUnit.SECONDS);
                    logger.info("\n" + "将空值存到缓存");
                }
            }
        } catch (Throwable t) {
            logger.warn("警告信息" + t.getMessage());
            t.printStackTrace();
        }
        return user;
    }

    @Override
    public String upHeadPortrait(MultipartFile file, String username) throws ImgException {
        if (file == null || file.getSize() <= 0) {
            throw new ImgException("头像不能为空");
        }
        String name = osscosUtil.uploadImgOSS(file);
        String imgUrl = osscosUtil.getImgUrl(name);
        User user=new User();
        user.setUsername(username);
        user.setImage(imgUrl);
        //上传的同时把图片url更新到数据库中
        userMapper.upHeadPortrait(user);
        return imgUrl;
    }

    @Override
    public User selectByPrimaryKey(Integer id){
        return userMapper.selectByPrimaryKey(id);
    }
}


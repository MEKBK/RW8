package com.kbk.util;

import com.kbk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RMIServiceChoice {
    private Logger logger= LoggerFactory.getLogger(RMIServiceChoice.class);
    UserService userService=null;

    public UserService getService(){

        int count=new Random().nextInt(2);
       // int count = 0;
        if(count == 0){
            try{
                logger.info("尝试获取serverA的服务");
                userService=getServiceA();
                logger.info("已经获取serverA的服务");

            }catch (Exception e){
                logger.error("服务A挂掉了,报错信息" + e.getMessage());
                e.printStackTrace();
                try {
                    logger.info("serverA的服务获取失败,获取serviceB的服务");
                    logger.info("尝试获取serverB的服务");
                    userService=getServiceB();
                    logger.info("已经获取serverB的服务");
                }catch (Exception e1){
                    logger.error("服务全部挂掉了,报错信息" + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        }else if(count == 1){
            try{
                logger.info("尝试获取serverB的服务");
                userService=getServiceB();
                logger.info("已经获取serverB的服务");
            }catch (Exception e){
                logger.error("服务B挂掉了,报错信息" + e.getMessage());
                e.printStackTrace();
                try {
                    logger.info("serverB的服务获取失败,获取serviceA的服务");
                    logger.info("尝试获取serverA的服务");
                    userService=getServiceA();
                    logger.info("已经获取serverA的服务");
                }catch (Exception e1){
                    logger.error("服务全部挂掉了,报错信息" + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        }
        return userService;
    }
    private UserService getServiceA(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("spring/spring-RMIClient.xml");
        return (UserService) applicationContext.getBean("UserServiceA");
    }
    private UserService getServiceB(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("spring/spring-RMIClient.xml");
        return (UserService) applicationContext.getBean("UserServiceB");
    }
}

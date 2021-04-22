package com.kbk.domain;

import com.kbk.service.Impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/04/18 10:14
 * @Version 1.0
 *
 */

public class ServerDoA {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public static void main(String[] args) {

        try {
          //  System.setProperty("java.rmi.server.hostname", "8.129.226.29");
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
           //  System.setProperty("java.rmi.server.hostname","172.21.0.2");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/spring-mybatis.xml");
            applicationContext.getBean("UserServiceImpl");
            logger.info("\n" + "spingservice服务已经启动，请准备！");
        } catch (Exception e) {
            logger.error("报错信息" + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.kbk.util;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;


/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/03/28 15:27
 * @Version 1.0
 *
 */

public class InterceptUtil implements HandlerInterceptor {


    private static Logger logger = LoggerFactory.getLogger(InterceptUtil.class);


    //密钥
    private static final String SECRE="1234";


    /**
     * 在控制器执行之前完成业务逻辑操作
     * 方法的返回值决定逻辑是否继续执行， true，表示继续执行， false, 表示不再继续执行。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        //从请求中获取cookie, 判断当前用户是否已经登陆
        Cookie cookie = CookieUtil.getCookie(request.getCookies(), "token");
        //判断客户端是否有cookie
        if (cookie != null) {
            //从cookie得到token
            String value = URLDecoder.decode(cookie.getValue(), "utf-8");
            logger.info("从客户端得到的taken：" + value);
            //解密token
            Claims token = JWTUtil.parseJWT(value, SECRE);
            logger.info("解密的taken：" + token);
            if(token!=null){
                return true;
            }else {
                return false;
            }
        } else {
            String headertoken = request.getHeader("token");
            logger.info("从客户端得到的taken：" + headertoken);
            //解密token
            Claims token = JWTUtil.parseJWT(headertoken, SECRE);
            if (token  != null){
                return true;
            }else {
                logger.info("用户没有登录");
                //如果没有检测到登录状态就重定向到登录界面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
    }


    /**
     * 在Controller方法调用之后执行，但是它会在DispatcherServlet进行视图返回渲染之前被调用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /**
     * 在完成视图渲染之后，执行此方法。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }



}


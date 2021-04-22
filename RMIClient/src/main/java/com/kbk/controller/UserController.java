package com.kbk.controller;


import com.kbk.Rest.Restful;
import com.kbk.model.User;
import com.kbk.service.UserService;
import com.kbk.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/03/28 10:14
 * @Version 1.0
 *
 */

@Controller
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private RMIServiceChoice rmiServiceChoice;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    TxSMSUtil txSMSUtil;
    @Autowired
    AliMailUtil aliMailUtil;

    UserService userService = null;

    private static final String KEY="1234";

    @RequestMapping(value = "/loginSuccessful", method = RequestMethod.GET)
    public String LoginSuccessful() {
        return "loginSuccessful";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/login/regist", method = RequestMethod.GET)
    public String loginRegist() {
        return "phoneRegist";
    }

    @RequestMapping(value = "/u/upload/image", method = RequestMethod.GET)
    public String uploadImg() {
        return "uploadFile";
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, Model model, HttpServletResponse response) throws UnsupportedEncodingException {

        //获取账号密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        long startTimeMillis = System.currentTimeMillis();
        userService = rmiServiceChoice.getService();

        User user = userService.selectUserName(username);
        long execTimeMillis = System.currentTimeMillis() - startTimeMillis;
        logger.info("方法名称 : UserController-Login---> 方法用时 : " + execTimeMillis+" ms");
        //验证该用户是否存在
        if (user != null) {
            logger.info("记录user的值： " + user);
            //判断密码是否相等
            String password1 = user.getPassword();
            logger.info("记录password1的值： " + password1);
            String passwordMd5 =DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
            logger.info("记录passwordMd5的值： " + passwordMd5);
            boolean flag = passwordMd5.equals(password1);

            if (flag) {
                //生成token
                String token = JWTUtil.getJWT(String.valueOf(user.getId()), user.getUsername(), new Date(), KEY);
                //把token装到cookie中发送到客户端
                CookieUtil.setCookie(response, "token", token, 600 * 600);
                return "redirect:loginSuccessful";
            } else {
                model.addAttribute("msg", "密码错误");
                return "login";
            }
        } else {
            logger.info("记录user的值： " + user);
            model.addAttribute("msg", "用户名错误或者用户不存在");
            return "login";
        }

    }



    @RequestMapping(value = "/phone/regist", method = RequestMethod.GET)
    public String regist() {
        return "phoneRegist";
    }
    /**
     * 手机注册接口
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/phone/regist", method = RequestMethod.POST)
    public String doPhoneRegist(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        //获取表单信息
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");
        String phone = request.getParameter("phone");
        String code = request.getParameter("code");
        User user1 = userService.selectUserName(username);
        User user2 = userService.selectUserName(phone);
        if (redisUtil.get(phone) != null) {
            if (code.equals(redisUtil.get(phone))) {
                /**
                 * 手机注册
                 * 判断用户名是否已经存在和比对验证码是否正确
                 */
                if (user1 == null && user2 == null) {
                    User userPhone = new User();
                    userPhone.setPhoneNumber(phone);
                    userPhone.setUsername(username);
                    //md5加密密码
                    String md5Password= DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
                    userPhone.setPassword(md5Password);
                    userPhone.setPassword(password);
                    userPhone.setCreateAt(System.currentTimeMillis());
                    userPhone.setUpdateAt(System.currentTimeMillis());
                    userService.insertSelective(userPhone);
                    return "login";
                } else {
                    model.addAttribute("msg", "该用户名或者手机号已经被注册");
                    return "phoneRegist";
                }
            } else {
                model.addAttribute("msgCode", "输入的验证码错误");
                return "phoneRegist";
            }
        } else {
            model.addAttribute("magCode", "验证码已经过期");
            return "phoneRegist";
        }

    }


    /**
     * 发送手机验证码接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/phone/code", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sentPhoneCode(@RequestParam("phone") String phone) {
        Map<String, Object> map = new HashMap<>(100);
        //验证手机号码格式
        String phoneRegex = "^((\\+86)|(86))?1[3|4|5|7|8][0-9]\\d{4,8}$";
        Pattern p = Pattern.compile(phoneRegex);
        Matcher m = p.matcher(phone);
        boolean isPhone = m.matches();
        //判断手机号格式，如果不对给页面显示错误信息
        if (!isPhone) {
            logger.info("手机格式不对" + phone);
            map.put("msg", "手机格式不对");
        } else {
            txSMSUtil.sendPhoneCode(phone, map);
        }
        return map;
    }

    @RequestMapping(value = "/mail/regist", method = RequestMethod.GET)
    public String MailRegist() {
        return "mailRegist";
    }


    /**
     * 邮箱注册接口
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/mail/regist", method = RequestMethod.POST)
    public String domailRegist(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        //获取表单信息
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");
        String email = request.getParameter("mail");
        String code = request.getParameter("code");
        User user1 = userService.selectUserName(username);
        User user2 = userService.selectUserName(email);
        if (redisUtil.get(email) != null) {
            if (code.equals(redisUtil.get(email))) {
                /**
                 * 邮箱注册
                 * 判断用户名是否已经存在和比对验证码是否正确
                 */
                if (user1 == null && user2 == null) {
                    User userMail = new User();
                    userMail.setEmail(email);
                    //md5加密密码
                    String md5Password=DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
                    userMail.setUsername(username);
                    userMail.setPassword(md5Password);
                    userService.insertSelective(userMail);
                    return "login";
                } else {
                    model.addAttribute("msg", "用户名或者邮箱已存在");
                    return "mailRegist";
                }
            } else {
                model.addAttribute("msgCode", "验证码输入错误");
                return "mailRegist";
            }
        } else {
            model.addAttribute("magCode", "验证码已经过期");
            return "mailRegist";
        }
    }

    /**
     * 发送邮箱验证码接口
     *
     * @param mail 邮箱号码
     * @return
     */
    @RequestMapping(value = "/mail/code", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sentMailCode(@RequestParam(value = "mail", required = false) String mail) {
        Map<String, Object> map = new HashMap<>(100);
        //验证邮箱
        String mailRegex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern p1 = Pattern.compile(mailRegex);
        Matcher m1 = p1.matcher(mail);
        boolean isMail = m1.matches();
        //判断邮箱号格式，如果不对给页面显示错误信息
        if (!isMail) {
            logger.info("邮箱格式不对"+mail);
            map.put("msg", "邮箱格式错误");
        } else {
            aliMailUtil.sendMailCode(mail, map);
        }
        return map;
    }

    /**
     * 上传头像接口
     * @param file
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/u/upload/image",method = RequestMethod.POST)
    public String uploadImg(@RequestParam MultipartFile file, Model model, HttpServletRequest request){
        try {
            userService = rmiServiceChoice.getService();
            String head = userService.upHeadPortrait(file, request.getParameter("username"));
            model.addAttribute("data", head);
        } catch (ImgException Img) {
            logger.error("报错信息Img: " + Img.getMessage());
            Img.printStackTrace();
        } catch (IOException ioe) {
            logger.error("报错信息ioe: " + ioe.getMessage());
            ioe.printStackTrace();
        }
        return "uploadFile";
    }

    /**
     * 根据id查询人物
     * @param
     * @return
     */
    @RequestMapping(value = "/UserJson/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> UserJson(@PathVariable Integer id) throws Exception {
        long startTimeMillis = System.currentTimeMillis();
        userService = rmiServiceChoice.getService();
        User user = userService.selectByPrimaryKey(id);
        long execTimeMillis = System.currentTimeMillis() - startTimeMillis;
        logger.info("方法名称 : UserController-UserJson---> 方法用时 : " + execTimeMillis+"ms");
        if (null == user) {
            return Restful.set(400, "查找职业失败" );
        } else {
            return Restful.set(200, "查找职业成功", user);
        }

    }
}


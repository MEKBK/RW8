package com.kbk.util;


import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
   * 邮件发送工具类
   */
public class MailUtils {
      public static String sendMail(String to, String vcode) throws Exception{
          String reStr = "";//定义返回值
          Properties properties = System.getProperties();
//          properties.setProperty("mail.smtp.auth", "true");//开启认证
//          properties.setProperty("mail.debug", "true");//启用调试
//          properties.setProperty("mail.smtp.timeout", "1000");//设置链接超时
//          properties.setProperty("mail.smtp.port", "587");//设置端口
//          properties.setProperty("mail.smtp.socketFactory.port", "587");//设置ssl端口
//          properties.setProperty("mail.smtp.socketFactory.fallback", "false");
//          properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
          //设置发送邮件的主机 smtp.qq.com
          String host = "smtp.qq.com";
          //1.创建连接对象，连接到邮箱服务器.Properties用来设置服务器地址，主机名
        //  Properties props = System.getProperties();
          //2.设置邮件服务器
          properties.setProperty("mail.smtp.hotst",host);
          properties.put("mail.smtp.auth","true");
          //SSL加密
          MailSSLSocketFactory sf = new MailSSLSocketFactory();
          sf.setTrustAllHosts(true);
          properties.put("mail.smtp.ssl.enable","true");
          properties.put("mail.smtp.ssl.socketFactory", sf);
          //props：用来设置服务器地址，主机名；Authenticator：认证信息
          Session session = Session.getDefaultInstance(properties, new Authenticator() {
              @Override
              //通过密码认证信息
              protected PasswordAuthentication getPasswordAuthentication(){
                  return new PasswordAuthentication("1594023884@qq.com","yygfwyyuxotwjjgh");
              }
          });
          try {
              Message message = new MimeMessage(session);
              //2.1设置发件人：
              message.setFrom(new InternetAddress("1594023884@qq.com"));
              //2.2设置收件人 这个TO就是收件人
              message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
              //2.3邮件的主题
              message.setSubject("来自MEKBK验证码邮件");
              //2.4设置邮件的正文 第一个参数是邮件的正文内容 第二个参数是：是文本还是html的连接
              message.setContent("<h1>来自MEKBK验证码邮件,请接收你的验证码：</h1><h3>你的验证码是："+vcode+"，请妥善保管好你的验证码！</h3>", "text/html;charset=UTF-8");
              //3.发送一封激活邮件
              Transport.send(message);
          }catch(MessagingException mex){
              mex.printStackTrace();
          }
          return reStr;
      }
}

package com.kbk.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;


@Component
public class AliMailUtil {
    private static Logger logger = LoggerFactory.getLogger(TxSMSUtil.class);
    @Autowired
    private RedisUtil redisUtil;

    //HtmlTest
//    private static final String[] EmailTemplate = {"static/mail_register_template.html"};
//    private static final String[] EmailTitle = {"【思青争】注册邮箱验证"};


    /**
     * 密钥
     */
    @Value("${AliMail.acceKeyId}")
    private String accessKeyId;
    @Value("${AliMail.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 邮箱业务
     */
    @Value("${AliMail.accountName}")
    private String accountName;
    @Value("${AliMail.fromAlias}")
    private String fromAlias;
    @Value("${AliMail.tagName}")
    private String tagName;
    @Value("${AliMail.subject}")
    private String subject;

    /**
     * 验证码
     */
    public String getCode() {
        String str="0123456789";
        StringBuilder st=new StringBuilder(4);
        for(int i=0;i<4;i++){
            char ch=str.charAt(new Random().nextInt(str.length()));
            st.append(ch);
        }
        String code=st.toString().toLowerCase();
        return code;
    }

    /**
     * 邮件发送
     */
    public void sendMail(String mail,String code) {
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的”cn-hangzhou”替换为”ap-southeast-1”、或”ap-southeast-2”。
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        //try {
        //DefaultProfile.addEndpoint(“dm.ap-southeast-1.aliyuncs.com”, “ap-southeast-1”, “Dm”,  “dm.ap-southeast-1.aliyuncs.com”);
        //} catch (ClientException e) {
        //e.printStackTrace();
        //}
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            //request.setVersion(“2017-06-22”);// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
//            “控制台创建的发信地址”
            request.setAccountName(accountName);
//            “发信人昵称”
            request.setFromAlias(fromAlias);
            request.setAddressType(1);
//            “控制台创建的标签”
            request.setTagName(tagName);
            request.setReplyToAddress(true);
//            “目标地址”
            request.setToAddress(mail);
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress(“邮箱1,邮箱2”);
//            “邮件主题”
            request.setSubject(subject);
            //如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
            //注意：文本邮件的大小限制为3M，过大的文本会导致连接超时或413错误
            //“邮件正文”
            request.setHtmlBody(code);
            //SDK 采用的是http协议的发信方式, 默认是GET方法，有一定的长度限制。
            //若textBody、htmlBody或content的大小不确定，建议采用POST方式提交，避免出现uri is not valid异常
            request.setSysMethod(MethodType.POST);
            //开启需要备案，0关闭，1开启
            //request.setClickTrace(“0”);
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);

        } catch (ServerException e) {
            //捕获错误异常码
            logger.info("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        } catch (ClientException e) {
            //捕获错误异常码
            logger.info("ErrCode :" + e.getErrCode());
            e.printStackTrace();
        }

    }


    /**
     * 封装发送邮箱验证码的方法
     * @param  mail
     * @param map
     */
    public void sendMailCode(final String mail, Map<String,Object> map){
        //判断今天是不是首次登录
        if (redisUtil.get("count" + mail) == null) {
            redisUtil.set("count" + mail, "5", 60 * 60 * 24);
            //生成验证码，加到redis中.设置失效时间为2分钟。
            String verify = getCode();
            //发送*********
            sendMail(mail,verify);
            logger.info("验证码是：" + verify);
            redisUtil.set(mail, verify, 60);
            map.put("msg", "验证码发送成功,还可以发送" + redisUtil.get("count"+mail) + "次");
        } else {
            //过期时间
            long expire = redisUtil.getExpire(mail);
            logger.info("失效时间：---" + expire);
            if (redisUtil.get(mail) == null) {
                int count = Integer.parseInt((String) redisUtil.get("count" + mail));
                //判断今天还有没有剩余发送次数和间隔时间
                if (count > 0) {
                    //生成验证码，加到redis中.设置失效时间为2分钟。
                    String verify = getCode();
                    //发送*********
                    sendMail(mail,verify);
                    logger.info("验证码是：" + verify);
                    redisUtil.set(mail, verify, 300);
                    //可发送次数-1
                    redisUtil.decr("count" + mail, 1);
                    map.put("msg", "验证码发送成功,还可以发送" + count + "次");
                } else {
                    logger.info(mail+ "====今天的发送验证码次数已使用完");
                    map.put("msg", "今天的发送次数已经用完");
                }
            } else {
                map.put("msg", "请隔" + expire + "秒再发送");
            }
        }
    }


}

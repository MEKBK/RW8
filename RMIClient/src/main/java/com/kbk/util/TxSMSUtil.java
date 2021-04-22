package com.kbk.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class TxSMSUtil {
    private static Logger logger = LoggerFactory.getLogger(TxSMSUtil.class);
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 密钥
     */
    @Value("${TxSMS.secretId}")
    private String secretId;
    @Value("${TxSMS.secretKey}")
    private String secretKey;

    /**
     * 短信业务
     */

    @Value("${TxSMS.sign}")
    private String sign;
    @Value("${TxSMS.smsSdkAppId}")
    private String smsSdkAppId;
    @Value("${TxSMS.TemplateID}")
    private String TemplateID;

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

    public void sendSMS(String telephone,String code) {

        logger.info("电话号码"+telephone);
        logger.info("验证码"+code);
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey，见《创建secretId和secretKey》小节
        Credential cred = new Credential(secretId, secretKey);

        // 实例化要请求产品(以cvm为例)的client对象
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod(ClientProfile.SIGN_TC3_256);

        //第二个ap-chongqing 填产品所在的区
        SmsClient smsClient = new SmsClient(cred, "ap-guangzhou");
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //appId ,见《创建应用》小节
        sendSmsRequest.setSmsSdkAppid(smsSdkAppId);
        //发送短信的目标手机号，可填多个。
        String[] phones={telephone};
        sendSmsRequest.setPhoneNumberSet(phones);
        //模版id,见《创建短信签名和模版》小节
        sendSmsRequest.setTemplateID(TemplateID);
        //模版参数，从前往后对应的是模版的{1}、{2}等,见《创建短信签名和模版》小节
        String [] templateParam={code,"1"};
        sendSmsRequest.setTemplateParamSet(templateParam);
        //签名内容，不是填签名id,见《创建短信签名和模版》小节
        sendSmsRequest.setSign(sign);
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse= smsClient.SendSms(sendSmsRequest); //发送短信
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        if (SendSmsResponse.toJsonString(sendSmsResponse) != null) {
            logger.info("发送成功");
            logger.info("短信接口返回的数据----------------");
            logger.info("StatusSet=" +SendSmsResponse.toJsonString(sendSmsResponse));
            logger.info("RequestId=" + sendSmsResponse.getRequestId());

        } else {
            logger.error("发送失败");
            logger.info("短信接口返回的数据----------------");
            logger.info("StatusSet=" + SendSmsResponse.toJsonString(sendSmsResponse));
            logger.info("RequestId=" + sendSmsResponse.getRequestId());
        }
    }

    /**
     * 封装发送手机验证码的方法
     * @param phone
     * @param map
     */
    public void sendPhoneCode(final String phone, Map<String,Object> map){
        //判断今天是不是首次登录
        if (redisUtil.get("count" + phone) == null) {
            redisUtil.set("count" + phone, "5", 60 * 60 * 24);
            //生成验证码，加到redis中.设置失效时间为2分钟。

            String verify = getCode();
            //发送********
            sendSMS(phone,verify);
            logger.info("验证码是：" + verify);
            redisUtil.set(phone, verify, 60);
            map.put("msg", "验证码发送成功,还可以发送" + redisUtil.get("count"+phone) + "次");
        } else {
            //过期时间
            long expire = redisUtil.getExpire(phone);
            logger.info("失效时间：---" + expire);
            //判断今天还有没有剩余发送次数和间隔时间
            if (redisUtil.get(phone) == null) {
                int count=Integer.parseInt((String)redisUtil.get("count"+phone));
                if(count>0) {
                    //生成验证码，加到redis中.设置失效时间为2分钟。
                    String verify = getCode();
                    //发送********
                    sendSMS(phone,verify);
                    logger.info("验证码是：" + verify);
                    redisUtil.set(phone, verify,300);
                    //可发送次数-1
                    redisUtil.decr("count" + phone, 1);
                    map.put("msg", "验证码发送成功,还可以发送" + redisUtil.get("count"+phone) + "次");
                } else {
                    logger.info(phone+ "====今天的发送验证码次数已使用完");
                    map.put("msg", "今天的发送次数已经用完");
                }
            } else {
                map.put("msg", "请隔" + expire + "秒再发送");
            }
        }
    }


}

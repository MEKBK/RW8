import com.kbk.util.TxSMSUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


public class TenInformationTest {
    private static Logger logger = LoggerFactory.getLogger(TenInformationTest.class);

    @Test
    public void test1(){
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey，见《创建secretId和secretKey》小节
        Credential cred = new Credential("AKIDau0vbKyEOQDn9GltLajdIo7G9nCXRU9a", "BAuPkqiLr0ta3JzQfnAUbT4QZSvA9tPf");

        // 实例化要请求产品(以cvm为例)的client对象
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod(ClientProfile.SIGN_TC3_256);
        //第二个ap-chongqing 填产品所在的区
        SmsClient smsClient = new SmsClient(cred, "ap-chongqing");

        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //appId ,见《创建应用》小节
        sendSmsRequest.setSmsSdkAppid("1400500060");
        //发送短信的目标手机号，可填多个。
        String[] phones={"+8618529143712"};
        sendSmsRequest.setPhoneNumberSet(phones);
        //模版id,见《创建短信签名和模版》小节
        sendSmsRequest.setTemplateID("906359");
        //模版参数，从前往后对应的是模版的{1}、{2}等,见《创建短信签名和模版》小节
        String [] templateParam={"4590","1"};
        sendSmsRequest.setTemplateParamSet(templateParam);
        //签名内容，不是填签名id,见《创建短信签名和模版》小节
        sendSmsRequest.setSign("思青争");
//        try {
//            SendSmsResponse sendSmsResponse= smsClient.SendSms(sendSmsRequest); //发送短信
//            System.out.println(sendSmsResponse.toString());
//        } catch (TencentCloudSDKException e) {
//            e.printStackTrace();
//        }

        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse= smsClient.SendSms(sendSmsRequest); //发送短信
            System.out.println(sendSmsResponse.toString());
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        if (sendSmsResponse.getSendStatusSet() != null) {
            logger.info("发送成功");
            logger.info("短信接口返回的数据----------------");
            logger.info("StatusSet=" + sendSmsResponse.getSendStatusSet());
            logger.info("RequestId=" + sendSmsResponse.getRequestId());

        } else {
            logger.error("发送失败");
            logger.info("短信接口返回的数据----------------");
            logger.info("StatusSet=" + sendSmsResponse.getSendStatusSet());
            logger.info("RequestId=" + sendSmsResponse.getRequestId());
        }
    }

    @Test
    public void test2(){
        int n = 20;
        for(int i = 0; i < n; i--){
            System.out.printf(""+i);
            System.out.printf("-");
        }
    }



    @Test
    public void RETest() {
        String a = "+8618529143712";
        String regex = "^((\\+86)|(86))?1[3|4|5|7|8][0-9]\\d{4,8}$";
        System.out.println(a.matches(regex));
    }

    @Test
    public void Test4() {
        TxSMSUtil txSMSUtil = new TxSMSUtil();
        txSMSUtil.sendSMS("+8618529143712","1235");
        System.out.println("1111");
    }
}

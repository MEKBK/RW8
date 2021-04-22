import com.kbk.util.OSSCOSUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

//让测试运行于Spring测试环境
@RunWith(SpringJUnit4ClassRunner.class)
//Spring整合JUnit4测试时，使用注解引入多个配置文件
@ContextConfiguration(locations = {"classpath:spring/spring-mvc.xml", "classpath:spring/spring-RMIClient.xml"})
public class OssAndCosTest {
    @Resource
    OSSCOSUtil osscosUtil;


    @Test
    public void test1(){
        osscosUtil.OssAndCos("D:\\JAVASourceCode\\SpringMVC-Task7\\src\\main\\webapp\\file\\image\\63920920_p0_master1200.jpg");
    }

    @Test
    public void test2(){
        osscosUtil.downLoad("D:\\JAVASourceCode\\SpringMVC-Task7\\src\\main\\webapp\\file\\");
    }

    @Test
    public void test3(){
        osscosUtil.getImgUrl("63920920_p0_master1200.jpg");
    }
}

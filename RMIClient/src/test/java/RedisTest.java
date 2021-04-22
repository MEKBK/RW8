import com.kbk.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


/**
 *
 * @Description
 * @Author 况博凯
 * @Date 2021/03/28 14:14
 * @Version 1.0
 *
 */

// 让测试运行于spring测试环境
@RunWith(SpringJUnit4ClassRunner.class)
// 指定 Spring 配置文件所在的位置
@ContextConfiguration(locations = "classpath:redis.xml")
public class RedisTest {

    @Resource
    RedisUtil redisUtil;

    @Test
    public void test1(){
//        if(!redisUtil.hasKey("test:01")){
//            redisUtil.set("test:01","这是一个测试redis缓存");
//
//        }
        System.out.println(redisUtil.get("MEKBK"));
    }

    @Test
    public void test2(){
        if(redisUtil.get("MEKBK") != null){
            redisUtil.del("MEKBK");

        }
        System.out.println(redisUtil.get("MEKBK"));
    }

}

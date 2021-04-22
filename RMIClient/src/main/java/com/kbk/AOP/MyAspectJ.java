package com.kbk.AOP;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyAspectJ
 * @Description
 * @Author 况博凯
 * @Date 2021/02/23 22：12
 * @Version 1.0
 */


@Aspect
@Component
public class MyAspectJ {
    private static final Logger logger = LoggerFactory.getLogger(MyAspectJ.class);

    /**
     * 定义一个切入点
     */
    @Pointcut("execution(* com.kbk.controller.*Controller.*(..))")
    private void logRecordArg(){};
    @Pointcut("execution(* com.kbk.controller.*Controller.*(..)) || execution(* com.kbk.service.*Service.*(..))")
    private void logRecordServiceTime(){};
//    @Pointcut("execution(* com.kbk.controller.*Controller.*(..)) || execution(* com.kbk.util.WebUtil.*(..))")
//    private void logWebUtil(){};
    /**
     * 定义一个环绕通知记录controller出参入参
     */
    @Around("logRecordArg()")
    private Object getControllerLog(ProceedingJoinPoint point) {
        //获取目标方法所在类的全限定名。
        String methodName=point.getSignature().getDeclaringType().getName();
        StringBuilder sb = null ;
        sb = new StringBuilder(300);
        // 类名+方法名
        String target = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();
        Object[] args = point.getArgs();
        String[] paramsName =  ((MethodSignature) point.getSignature()).getParameterNames();

        sb.append(target).append(" 入参:【");
        if(args != null && paramsName != null && args.length > 0 && paramsName.length > 0) {
            for (int i=0; i< paramsName.length; i++) {
                sb.append(" ").append(paramsName[i]).append(" = ").append(args[i]).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("】");

        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            logger.error("错误信息:"+throwable.getMessage());
        }
        sb.append(" 出参:【").append(result).append("】");
        // 记录日志
        logger.info(sb.toString());
        // 调用结果返回
        return result;

    }
    @Around("logRecordServiceTime()")
    public Object around(ProceedingJoinPoint joinPoint)  {
        String methodName = joinPoint.getSignature().getName();

        logger.info("Method Name : [" + methodName + "] ---> AOP around start");
        long startTimeMillis = System.currentTimeMillis();
        //调用 proceed() 方法才会真正的执行实际被代理的方法
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("错误信息"+throwable.getMessage());
        }
        long execTimeMillis = System.currentTimeMillis() - startTimeMillis;
        logger.info("方法名称 : [" + methodName + "] ---> 方法用时 : " + execTimeMillis+"ms");
        return result;

    }
//    @Around("logWebUtil()")
//    public Object webUtil(ProceedingJoinPoint pjp){
//        String methodName = pjp.getSignature().getName();
//        logger.info("Method Name : [" + methodName + "] ---> AOP around start"+"测试one");
//
//        Object result = null;
//
//        try {
//            result = pjp.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return result;
//
//    }

}




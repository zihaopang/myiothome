package com.myiothome.controller.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这个类使用了AOP方式对某一个类进行日志记录
 */
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * execution(* com.myiothome.service.*.*(..))
     * 第一个*号，所有的返回值
     * com.myiothome.service：service这个包下
     * 第二个*：所有的类
     * 第三个*：该类下所有的方法
     * (..)：所有的参数
     * 都要进行切面的处理
     */
    //@Pointcut("execution(* com.myiothome.service.*.*(..))")
    public void pointcut(){
    }

    /**
     * 前置切入pointcut切入点
     * @param joinPoint：切入点的函数
     */
    //@Before("pointcut()")
    public void before(JoinPoint joinPoint){
        /**
         * 比如要对所有的service记录如下的信息：
         * 用户[用户IP]，在[访问时间]，访问了[方法名]
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteAddr();//获取IP地址的方法

            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String methodName = joinPoint.getSignature().getDeclaringTypeName()+
                    joinPoint.getSignature().getName();

            logger.info(String.format("用户【%s】,在【%s】,访问了【%s】",ip,time,methodName));
        }

    }

    /**
     * 另外计中织入方式
     */
//    @After("pointcut()")
//    public  void after(){}
//
//    @Around("pointcut()")
//    public  void around(){}
//
//    @AfterReturning("pointcut()")
//    public  void afterReturning(){}
//
//    @AfterThrowing("pointcut()")
//    public  void afterThrowing(){}
}

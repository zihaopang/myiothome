package com.myiothome.controller.advice;

import com.myiothome.util.MyIotHomeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 这个类的功能在于，所有打上Controller注解的类，发生错误之后
 * 都会被该类捕捉，从而返回响应的页面，而不是一堆乱码
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(Exception.class);

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("服务器发生异常："+e.getMessage());
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");

        if(xRequestedWith.equals("XMLHttpRequest")){//是异步请求，返回字符串
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(MyIotHomeUtils.getJSONString(500,"服务器错误！"));
        }else{//是html请求，返回对应的错误页面
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}

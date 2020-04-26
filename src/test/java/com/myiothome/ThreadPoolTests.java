package com.myiothome;

import com.myiothome.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
//因为是测试类，所以需要使用和main中相同的环境
@ContextConfiguration(classes = MyiothomeApplication.class)
public class ThreadPoolTests {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    //JDK普通线程池
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK 可以执行定时任务的线程池
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //3.Spring普通线程池
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //4.Spring可执行定时任务的线程池
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    UserService userService;

    private void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //1.JDK普通线程池
    @Test
    public void testExecutorService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("testExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }

        sleep(10000);
    }

    //2.JDK可执行定时任务的线程池
    @Test
    public void testScheduleExecutorService() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("testScheduleExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(runnable, 10000, 2000, TimeUnit.MILLISECONDS);

        sleep(10000);
    }

    //3.Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.debug("threadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(runnable);
            sleep(1000);
        }

        sleep(10);
    }

    //4.可执行定时任务的Spring线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("threadPoolTaskScheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis()+10000);

        threadPoolTaskScheduler.scheduleAtFixedRate(runnable,startTime,1000);

        sleep(10000);
    }

    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for (int i = 0; i < 10; i++) {
            userService.execute1();
        }
    }
}
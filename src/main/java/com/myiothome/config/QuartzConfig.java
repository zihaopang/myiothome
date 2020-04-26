package com.myiothome.config;

import com.myiothome.quartz.AlphaJob;
import com.myiothome.quartz.PostScoreJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Quartz的配置类，该配置类初始化的信息被封装到数据库，Quartz再从数据库访问
 */
@Configuration
public class QuartzConfig {
    /**
     * FactoryBean可以简化Bean的实例化过程
     * 1.通过FactoryBean封装Bean的实例化过程
     * 2.将FactoryBean装配到Spring容器里
     * 3.将FactoryBean诸如给其他的Bean
     * 4.该Bean得到的是Factory所管理的对象实例
     */
    //配置JobDetail，JobDetail:用来描述Job实现类及其它相关的静态信息，如Job名字、关联监听器等信息。
    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaGroup");
        factoryBean.setDurability(true);
        return factoryBean;
    }

    //Trigger：触发器，用于定义任务调度的时间规则
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail jobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

    @Bean
    public JobDetailFactoryBean postScoreJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreJob.class);
        factoryBean.setName("postScoreJob");
        factoryBean.setGroup("postScoreGroup");
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreTrigger(JobDetail jobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setName("postScoreTrigger");
        factoryBean.setGroup("postScoreGroup");
        factoryBean.setRepeatInterval(5000);
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }
}

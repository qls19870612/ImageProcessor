package com.ejjiu.image.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PreDestroy;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/19 16:21
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(2));
    }

    @Override
    public void destroy() throws Exception {

    }

    @PreDestroy
    public void dispose() {

    }


}

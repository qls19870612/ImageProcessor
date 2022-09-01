package com.ejjiu.image.spring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/19 16:16
 */
@Service
public class TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestService.class);
    private AtomicInteger count = new AtomicInteger(0);


    //    @Scheduled(cron = "*/5 * * * * *")
    public void test() {
        int i = count.incrementAndGet();
        logger.debug("test count:{}", i);
    }

    //    @Scheduled(fixedDelay = 1000)
    //    public void test2() {
    //        int i = count.incrementAndGet();
    //        logger.debug("test count:{}", i);
    //    }
    //
    //    @Scheduled(fixedDelay = 1000)
    //    public void test3() {
    //        int i = count.incrementAndGet();
    //        logger.debug("test count:{}", i);
    //    }
}

package com.ejjiu.image;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/10/23 10:57
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.ejjiu.image.spring","com.ejjiu.image.table"})
@EntityScan("com.ejjiu.image.jpa.table")
@EnableScheduling
public class SpringMain {

}

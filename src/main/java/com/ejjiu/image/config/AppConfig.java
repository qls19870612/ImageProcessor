package com.ejjiu.image.config;

import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.spring.utils.SpringUtil;




public class AppConfig {
 
 
 

    public static ConfigRepository getConfigRepository() {
        return configRepository;
    }

    private static ConfigRepository configRepository;
 
 
 

    public static void initSqlLite() {

        configRepository = (ConfigRepository) SpringUtil.getBean("configRepository");

    }
 


}

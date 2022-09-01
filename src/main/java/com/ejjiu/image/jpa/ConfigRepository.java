package com.ejjiu.image.jpa;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.jpa.table.Config;
import com.ejjiu.image.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/16 14:25
 */
@Repository
public interface ConfigRepository extends JpaRepository<Config, String> {
    static final Logger logger = LoggerFactory.getLogger(ConfigRepository.class);
    //        @Query("select value from config where key=?1")
    
    default String getConfig(String key) {
        Config config = findByKey(key);
        String value;
        if (config != null) {
            value = config.getValue();
        }
        else
        {
            value = "";
        }
        return value;
    }
    default boolean hasConfig(ConfigType key)
    {
        return findByKey(key.name()) != null;
    }
    Config findByKey(String key);
    
    default String getConfig(ConfigType key) {
        //        logger.debug("getConfig key:{}", key);
        String config = getConfig(key.name());
        if (config == null) {
            return "";
        }
        return config;
    }
    
    default int setConfig(String key, String value) {
        Config config = new Config();
        config.setKey(key);
        config.setValue(value);
        Config save = save(config);
        if (save.getKey() != null) {
            return 1;
        }
        return 0;
    }
    
    default int setConfig(ConfigType key, String value) {
        return setConfig(key.name(), value);
    }
    
    default int getInt(ConfigType type) {
        String config = getConfig(type);
        return Utils.safeParseInt(config, 0);
    }
    
    default int getInt(ConfigType type, int defaultValue) {
        String config = getConfig(type);
        return Utils.safeParseInt(config, defaultValue);
    }
    
    default int setInt(ConfigType key, int value) {
        return setConfig(key, String.valueOf(value));
    }
}

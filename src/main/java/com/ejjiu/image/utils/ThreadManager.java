package com.ejjiu.image.utils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/07/29 16:44
 */
public class ThreadManager {
    public static int getDefaultThreadCount(){
//        return 1;
        return Runtime.getRuntime().availableProcessors();
    }
}

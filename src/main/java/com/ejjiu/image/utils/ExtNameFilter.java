package com.ejjiu.image.utils;

import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * 扩展名过滤器
 * 创建人  liangsong
 * 创建时间 2021/01/22 9:11
 */
public class ExtNameFilter {
    public ArrayList<String> whiteList = Lists.newArrayList();
    public ArrayList<String> blackList = Lists.newArrayList();
    public ExtNameFilter() {
    }
    public void initConfig(String extNameCfg)
    {
        whiteList.clear();
        blackList.clear();
        if (StringUtils.isNotEmpty(extNameCfg)) {
            String[] split = extNameCfg.split(";");
            for (String s : split) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                if (s.startsWith("!")) {
                    if (s.length() > 1) {
                        blackList.add(s.substring(1));
                    }
                }
                else
                {
                    whiteList.add(s);
                }
            }
        }
    }
    public boolean isEnableFile(String fileName)
    {
        for (String s : blackList) {
            if (fileName.endsWith(s)) {
                return false;
            }
        }
        if (whiteList.size() > 0) {
            for (String s : whiteList) {
                if (fileName.endsWith(s)) {
                    return true;
                }
            }
            return false;
        }
       return true;
    }
}

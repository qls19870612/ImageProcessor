package com.ejjiu.image.utils;

import java.util.Arrays;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/09/15 16:16
 */
public class TabUtils {
    public static final String TAB = "   ";
    public static String[] tabArr = new String[10];
    public static String getCountTAB(int count)
    {
        if (count >= tabArr.length) {
            tabArr = Arrays.copyOf(tabArr,count + 10);
        }
        if (tabArr[count] == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(TAB);
            }
            tabArr[count] = sb.toString();
        }
        return tabArr[count];

    }
}

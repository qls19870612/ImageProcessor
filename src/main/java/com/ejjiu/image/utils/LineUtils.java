package com.ejjiu.image.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/06/25 19:34
 */
public class LineUtils {
    private static final Logger logger = LoggerFactory.getLogger(LineUtils.class);
    static StringBuilder tempStringBuilder = new StringBuilder();
    public static int findLineNotEmptyStartIndex(String content,int startIndex)
    {
        int length = content.length();
        int index = startIndex;

        tempStringBuilder.setLength(0);
        for (int i = startIndex; i < length; i++) {
            char c = content.charAt(i);
            if (c == '\n') {
                index = i;
                break;
            }
            if (c ==' ') {
                continue;
            }

            if (c =='\t') {
                continue;
            }
            index = i;
            break;
        }
        return index;
    }

    public static int findLineNotEmptyEndIndex(String content,int endIndex)
    {
        int index = endIndex;

        tempStringBuilder.setLength(0);
        for (int i = endIndex; i >=0; i--) {
            char c = content.charAt(i);
            if (c == '\n') {
                index = i;
                break;
            }
            if (c ==' ') {
                continue;
            }

            if (c =='\t') {
                continue;
            }
            index = i;
            break;
        }
        return index;
    }

    public static void main(String[] args) {
        String a="{1\n" + "        int index = endIndex;\n" + "\n" + "        tempStringBuilder.setLength(0);\n" +
                "        for (int i = endIndex; i >=0; i--) {\n" + "            char c = content.charAt(i);\n" + "            if (c == '\\n') {\n" +
                "                index = i;\n" + "                break;\n" + "            }\n" + "            if (c ==' ') {\n" +
                "                continue;\n" + "            }\n" + "\n" + "            if (c =='\\t') {\n" + "                continue;\n" +
                "            }\n" + "            index = i;\n" + "            break;\n" + "        }\n" + "        return index;\n" + "33}";
        int lineNotEmptyStartIndex = findLineNotEmptyStartIndex(a, 1);
        int lineNotEmptyEndIndex = findLineNotEmptyEndIndex(a, a.length() - 1);
        String substring = a.substring(lineNotEmptyStartIndex, lineNotEmptyEndIndex);
        logger.debug("main substring:{}", substring);

    }
}

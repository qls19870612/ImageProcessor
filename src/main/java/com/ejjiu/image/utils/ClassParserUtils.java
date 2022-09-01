package com.ejjiu.image.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @描述
 * @创建人 liangsong
 * @创建时间 2018/7/10/010 16:28
 */
public class ClassParserUtils {
    private static final Pattern classPattern = Pattern.compile("public\\s+(?:\\w+\\s)*class\\s+(\\w+)");
    public static final Pattern packageNamePattern = Pattern.compile("package\\s+(([\\s\\S])+?);");
    public static final Pattern enumPattern = Pattern.compile("public\\s+enum\\s+(\\w+)");

    public static String getClassName(String javaContent) {
        Matcher matcher = classPattern.matcher(javaContent);
        if (matcher.find()) {
            return matcher.group(1);

        }
        return null;
    }

    public static ArrayList<String> getEnumNames(String javaContent) {
        Matcher matcher = enumPattern.matcher(javaContent);
        ArrayList<String> ret = new ArrayList<>();
        while (matcher.find()) {
            String enumName = matcher.group(1);
            ret.add(enumName);
        }
        return ret;
    }

    public static String getPackageName(String javaContent) {
        Matcher matcher = packageNamePattern.matcher(javaContent);
        if (matcher.find()) {
            String packageName = matcher.group(1);
            return packageName;
        }
        return "UNKNOW_PAKAGE";
    }

    public static String getPackageNoClassName(String packageName) {
        StringBuffer ret = new StringBuffer();
        String[] strings = packageName.split("\\.");
        int len = strings.length - 1;
        for (int i = 0; i < len; i++) {
            ret.append(strings[i]);
            if (i != len - 1) {
                ret.append(".");
            }
        }
        return ret.toString();
    }

    public static String getClassNameByFullPackName(String fullPackName) {
        String[] importArr = fullPackName.split("\\.");
        return importArr[importArr.length - 1];
    }
}

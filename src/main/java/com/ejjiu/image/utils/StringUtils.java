package com.ejjiu.image.utils;

import com.twmacinta.util.MD5;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;


/**
 * @描述
 * @创建人 liangsong
 * @创建时间 2018/ 07/2018/7/7/007 18:31
 */
public class StringUtils {
    private static Pattern pattern = Pattern.compile("\\$(\\d+)");
    private static Pattern number = Pattern.compile("\\d+");
    private static Pattern unicode = Pattern.compile("\"((?:\\\\\\d+)+)\"");
    //    private static Pattern worldPattern = Pattern.compile("[a-zA-Z0-9]+");
    private static Pattern worldPattern = Pattern.compile("[A-Z]{1,1}[a-z]{0,100}");
    private static Pattern UPPERCASE = Pattern.compile("[A-Z]+");
    private static Pattern multiComment = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    //    private static Pattern singleComment = Pattern.compile("^\\s*//[\\s\\S]*\\n");
    private static Pattern singleComment = Pattern.compile("\\s*//.*\\n", Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);
    
    
    public static String replace(String content, String... args) {
        
        Matcher matcher = pattern.matcher(content);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            matcher.appendReplacement(stringBuffer, args[index]);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
    
    public static String replaceKey(String content, String key, String value) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            Pattern p = Pattern.compile("\\$" + key);
            Matcher m = p.matcher(content);
            boolean isFind = false;
            while (m.find()) {
                isFind = true;
                m.appendReplacement(stringBuffer, value);
            }
            logger.error("replaceKey isFind:{}", isFind);
            
            m.appendTail(stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
    
    /**
     * 字符串转驼峰
     * @param s
     * @param separator 字符串分割符
     * @return
     */
    public static String toHump(String s, String separator) {
        String[] nameArr = s.split(separator);
        StringBuffer stringBuffer = new StringBuffer();
        int nameArrLen = nameArr.length;
        for (int i = 0; i < nameArrLen; i++) {
            if (nameArr[i].length() > 0) {
                if (stringBuffer.length() == 0) {
                    stringBuffer.append(Character.toLowerCase(nameArr[i].charAt(0)));
                } else {
                    stringBuffer.append(Character.toUpperCase(nameArr[i].charAt(0)));
                }
                stringBuffer.append(nameArr[i].substring(1));
            }
        }
        return stringBuffer.toString();
    }
    
    public static String toUpCase(String s) {
        return toUpCase(s, "_");
    }
    
    public static String toUpCase(String s, String separator) {
        Matcher mather = UPPERCASE.matcher(s);
        StringBuffer stringBuffer = new StringBuffer();
        int startIndex = 0;
        while (mather.find()) {
            String string = s.substring(startIndex, mather.start());
            appendUpper(stringBuffer, string, separator);
            stringBuffer.append(separator);
            startIndex = mather.start();
        }
        if (startIndex < s.length() - 1) {
            String string = s.substring(startIndex, s.length());
            appendUpper(stringBuffer, string, separator);
        }
        return stringBuffer.toString();
    }
    
    private static void appendUpper(StringBuffer stringBuffer, String string, String separator) {
        while (string.endsWith(separator)) {
            string = string.substring(0, string.length() - separator.length());
        }
        stringBuffer.append(string.toUpperCase());
    }
    
    public static boolean isEmpty(String string) {
        return string == null || string.equals("");
    }
    
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
    
    public static String toUpLowerString(String newValue) {
        return toUpLowerString(newValue, false);
    }
    
    public static String toUpLowerString(String newValue, boolean firstUpper) {
        StringBuilder ret = new StringBuilder();
        boolean hasBig = false;
        for (char c : newValue.toCharArray()) {
            if (isLetter(c)) {
                
                if (ret.length() == 0) {
                    hasBig = true;
                    if (firstUpper) {
                        ret.append(Character.toUpperCase(c));
                        
                        continue;
                    } else {
                        if (isBig(c)) {
                            ret.append(Character.toLowerCase(c));
                            continue;
                        }
                    }
                } else if (!hasBig) {
                    ret.append(Character.toUpperCase(c));
                    hasBig = true;
                    continue;
                }
                
                ret.append(c);
                
                
            } else {
                hasBig = false;
                if (isNumber(c)) {
                    ret.append(c);
                }
            }
        }
        return ret.toString();
    }
    
    public static boolean isLetter(char c) {
        return isSmall(c) || isBig(c);
    }
    
    public static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
    
    public static boolean isSmall(char c) {
        return c >= 'a' && c <= 'z';
    }
    
    public static boolean isBig(char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    private static byte[] getBytes(String s1) {
        
        String[] split = s1.split("\\\\");
        byte[] bytes2 = new byte[split.length - 1];
        int count = 0;
        for (String s : split) {
            if (s.equals("")) {
                continue;
            }
            int i = Integer.parseInt(s, 8);
            bytes2[count++] = (byte) i;
        }
        return bytes2;
    }
    
    public static String convertToString(String src) {
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = unicode.matcher(src);
        
        
        while (matcher.find()) {
            String group = matcher.group(1);
            byte[] bytes = getBytes(group);
            String string = null;
            try {
                string = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            matcher.appendReplacement(stringBuffer, "\"" + string + "\"");
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
    
    public static long safeParseLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        
        return defaultValue;
    }
    
    public static int safeParseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        
        return defaultValue;
    }
    
    public static String rightFill(String s, int i, String oneChar) {
        while (s.length() < i) {
            s = s + oneChar;
        }
        return s;
    }
    
    public static String leftFill(String s, int i, String oneChar) {
        while (s.length() < i) {
            s = oneChar + s;
        }
        return s;
    }
    
    public static boolean strIsNumber(String nodeValue) {
        if (StringUtils.isEmpty(nodeValue)) {
            return true;
        }
        
        for (int i = 0; i < nodeValue.length(); i++) {
            if (!isNumber(nodeValue.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static <T> String joinArray(T[] s, String split) {
        return joinArray(s, 0, split);
    }
    
    public static String joinArray(int[] s, String split) {
        return joinArray(s, 0, split);
    }
    
    public static <T> String joinList(List<T> s, String split) {
        return joinList(s, 0, split);
    }
    
    public static <T> String joinList(List<T> s, int startIndex, String split) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = startIndex; i < s.size(); i++) {
            stringBuilder.append(s.get(i)).append(split);
        }
        
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - split.length());
        }
        return stringBuilder.toString();
    }
    
    public static String joinArray(int[] s, int startIndex, String split) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = startIndex; i < s.length; i++) {
            stringBuilder.append(s[i]).append(split);
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - split.length());
        }
        return stringBuilder.toString();
    }
    
    public static <T> String joinArray(T[] s, int startIndex, String split) {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = startIndex; i < s.length; i++) {
            stringBuilder.append(s[i]).append(split);
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - split.length());
        }
        return stringBuilder.toString();
    }
    
    public static String removeComment(String s) {
        s = removeMultiComment(s);
        
        return removeSingleComment(s);
    }
    
    public static String removeSingleComment(String s) {
        StringBuffer sb;
        Matcher matcher1 = singleComment.matcher(s);
        sb = new StringBuffer();
        while (matcher1.find()) {
            matcher1.appendReplacement(sb, "\n");
        }
        matcher1.appendTail(sb);
        return sb.toString();
    }
    
    public static String removeMultiComment(String s) {
        Matcher matcher = multiComment.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    public static String replaceMatchLine(Pattern pattern, String s) {
        StringBuffer sb = new StringBuffer();
        
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "// " + matcher.group());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    public static String getMd5(String content) {
        return new MD5(content).asHex();
    }
    
    public static byte[] getMd5(byte[] content) {
        return new MD5(content).Final();
    }
    
    public static String getMd5ByBytes(byte[] content) {
        return new MD5(content).asHex();
    }
    
    public static void main(String[] args) {
        ArrayList<String> toLowerFirstChar = wordSplit("to_LowerXXX#$$FirstNEWChar");
        //        ArrayList<String> toLowerFirstChar = wordSplit("yIndex");
        //        ArrayList<String> toLowerFirstChar = wordSplit("N_NNxIndex__");
        for (String s : toLowerFirstChar) {
            logger.debug("main s:{}", s);
        }
        
    }
    
    public static String toLowerFirstChar(String propertyName) {
        return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
    
    public static String toUpperFirstChar(String propertyName) {
        return Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }
    
    public static String[] getAllLine(String rawContentTxt) {
        rawContentTxt = rawContentTxt.replace("\r", "\n");
        
        return rawContentTxt.split("\n");
    }
    
    public enum LetterType {
        UPPER_CASE,
        LOWER_CASE,
        NON;
    }
    
    public static String combineLastWord(String combineWords) {
        ArrayList<String> strings = wordSplit(combineWords);
        return strings.get(strings.size() - 1);
    }
    
    public static String onlyGetWord(String combineWords) {
        ArrayList<String> strings = wordSplit(combineWords);
        return combineStrList(strings);
    }
    
    @NotNull
    public static String combineStrList(ArrayList<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (sb.length() > 0) {
                boolean allBig = isAllBig(string);
                if (!allBig) {
                    string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
                }
            }
            sb.append(string);
        }
        return sb.toString();
    }
    
    public static boolean isAllBig(String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            if (!isBig(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static ArrayList<String> wordSplit(String combineWords) {
        
        ArrayList<String> list = new ArrayList<String>(5);
        int len = combineWords.length();
        LetterType type = LetterType.NON;
        int cutStart = 0;
        
        for (int i = 0; i < len; i++) {
            char c = combineWords.charAt(i);
            if (isBig(c)) {
                if (type != LetterType.UPPER_CASE) {
                    if (cutStart < i) {
                        String word = combineWords.substring(cutStart, i);
                        list.add(word);
                        cutStart = i;
                    }
                }
                type = LetterType.UPPER_CASE;
            } else if (isLetter(c)) {
                if (type == LetterType.UPPER_CASE) {
                    if (cutStart < i - 2) {
                        String word = combineWords.substring(cutStart, i - 1);
                        list.add(word);
                        cutStart = i - 1;
                    }
                }
                type = LetterType.LOWER_CASE;
            } else {
                if (type != LetterType.NON) {
                    String word = combineWords.substring(cutStart, i);
                    list.add(word);
                }
                cutStart = i + 1;
                type = LetterType.NON;
            }
        }
        if (type != LetterType.NON) {
            if (cutStart < len) {
                String word = combineWords.substring(cutStart, len);
                list.add(word);
            }
        }
        
        return list;
    }
    
    public static String formatHexString(Color c) {
        
        return String
                .format((Locale) null, "#%02x%02x%02x%02x", Math.round(c.getRed() * 255), Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255),Math.round(c.getOpacity() * 255));
        
    }
    
}

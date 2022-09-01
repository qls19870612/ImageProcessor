package com.ejjiu.image.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import static com.google.common.base.Preconditions.checkArgument;

public class Utils extends CommonUtils {
 
    public static final String BR = "\r\n";
 

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static String[] nums = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private static String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿"};

    private Utils() {
        super();
    }

    public static int getBitMask(int bitSize) {
        return (1 << bitSize) - 1;
    }

    public static void safeRun(Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            logger.error("safeRun: {}", e);
        }
    }

    public static boolean isBitSet(int val, int p) {
        checkArgument(p >= 0 && p <= 31);
        return (val & (1 << p)) != 0;
    }

    public static boolean isBitSet(long val, int p) {
        checkArgument(p >= 0 && p <= 63);
        return (val & (1 << p)) != 0;
    }

    public static int bitSet(int val, int p) {
        checkArgument(p >= 0 && p <= 31);
        return val | 1 << p;
    }

    public static long bitSet(long val, int p) {
        checkArgument(p >= 0 && p <= 63);
        return val | 1 << p;
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void printStackTrace(PrintStream ps) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            ps.println("\tat " + trace[i]);
        }
    }

    public static void printStackTraceToError(Logger logger) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            logger.error("\tat " + trace[i]);
        }
    }


    public static String getStackTrace() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        StringBuilder sb = new StringBuilder(128);
        for (StackTraceElement e : trace) {
            sb.append("\n\tat ");
            sb.append(e);
        }
        return sb.toString();
    }

    public static int getPointWithRange(int low, int high, int value) {
        if (value >= high) {
            return high;
        }
        if (value <= low) {
            return low;
        }
        return value;
    }

    public static long getPointWithRange(long low, long high, long value) {
        if (value >= high) {
            return high;
        }
        if (value <= low) {
            return low;
        }
        return value;
    }

    public static float getPointWithRange(float low, float high, float value) {
        if (value >= high) {
            return high;
        }
        if (value <= low) {
            return low;
        }
        return value;
    }

    public static double getPointWithRange(double low, double high, double value) {
        if (value >= high) {
            return high;
        }
        if (value <= low) {
            return low;
        }
        return value;
    }

    public static int minVarArg(int... a) {
        if (a.length == 0) {
            throw new IllegalArgumentException();
        }

        int min = a[0];

        for (int i = 1; i < a.length; i++) {
            if (a[i] < min) {
                min = a[i];
            }
        }

        return min;
    }

    public static int min(int a, int b, int c) {
        int min = a;
        if (b < min) {
            min = b;
        }
        if (c < min) {
            min = c;
        }
        return min;
    }

    public static boolean isLongFitsInInt(long x) {
        return (int) x == x;
    }

    /**
     * 计算花费. 单价必须>0, quantity必须>0, 如果x出来的结果超出了int, 抛错
     *
     * @param singlePrice
     * @param quantity
     * @return
     */
    public static int safeMultiplyInt(int singlePrice, int quantity) {
        if (singlePrice <= 0) {
            throw new IllegalArgumentException("safeMultiplyInt时, singlePrize必须>0: " + singlePrice);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("safeMultiplyInt时, quantity必须>0: " + quantity);
        }

        long longResult = safeMultiplyLong(singlePrice, quantity);

        int result = (int) longResult;
        if (longResult != result) {
            throw new IllegalArgumentException("safeMultiplyInt时, 乘出来的数overflow了. " + singlePrice + " x " + quantity);
        }

        assert result > 0; // 那个long肯定是>0的
        return result;
    }

    /**
     * 计算花费. 单价必须>0, quantity必须>0, 如果x出来的结果超出了Long.MAX_VALUE, 抛错
     *
     * @param singlePrice
     * @param quantity
     * @return
     */
    public static long safeMultiplyLong(long singlePrice, long quantity) {
        if (singlePrice <= 0) {
            throw new IllegalArgumentException("safeMultiplyLong时, singlePrize必须>0: " + singlePrice);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("safeMultiplyLong时, quantity必须>0: " + quantity);
        }

        if (Long.MAX_VALUE / singlePrice < quantity) {
            throw new IllegalArgumentException("safeMultiplyLong时, 乘出来的数overflow了. " + singlePrice + " x " + quantity);
        }

        return singlePrice * quantity;
    }

    public static boolean isInEasyRange(int x1, int y1, int x2, int y2, int range) {
        return Math.abs(x1 - x2) <= range && Math.abs(y1 - y2) <= range;
    }

    public static int getDistanceSquare(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public static int getEasyRange(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     * 在ai中找key，如果存在，返回该idx，如果不存在，返回第一个大于key的idx，如果key大于ai中最大值，返回ai.length -
     * 1（ai升序排列）
     */
    public static int binarySearchForCeilingKey(int[] ai, int key) {
        int low = 0;
        int high = ai.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = ai[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return low == ai.length ? low - 1 : low;
    }

    /**
     * 在ai中找key，如果存在，返回该idx，如果不存在，返回第一个小于key的idx，如果key小于ai中的最小值，返回0（ai升序排列）
     */
    public static int binarySearchForFloorKey(int[] ai, int key) {
        int low = 0;
        int high = ai.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = ai[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return low == 0 ? 0 : low - 1;
    }

    public static int binarySearchForFloorKey(long[] ai, long key) {
        int low = 0;
        int high = ai.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = ai[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return low == 0 ? 0 : low - 1;
    }

    //    public static byte[] readFile(String s) throws IOException{
    //        return readFile(new File(s));
    //    }
    //
    //    public static byte[] readFile(File file) throws IOException{
    //        if (!file.exists() || file.isDirectory()){
    //            return null;
    //        }
    //        return Files.toByteArray(file);
    //    }
    //
    //    /**
    //     * 将当前流中能读入的数据全部读入
    //     * 调用者自己关闭流
    //     * @setter in
    //     * @return
    //     */
    //    public static byte[] readFile(InputStream in){
    //        if (in == null){
    //            return null;
    //        }
    //        try{
    //            return ByteStreams.toByteArray(in);
    //        } catch (IOException e){
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }
    //
    //    public static byte[] readGzipFile(File file){
    //        if (!file.exists() || file.isDirectory()){
    //            return null;
    //        }
    //        try (InputStream in = new FileInputStream(file);
    //             GZIPInputStream is = new GZIPInputStream(in);){
    //            ByteArrayBuilder builder = new ByteArrayBuilder(8192 * 1024);
    //
    //            byte abyte0[] = new byte[2048 * 1024];
    //            int length = 0;
    //            while ((length = is.read(abyte0)) != -1){
    //                builder.append(Arrays.copyOf(abyte0, length));
    //            }
    //            return builder.toByteArray();
    //        } catch (IOException ioexception){
    //            ioexception.printStackTrace();
    //        }
    //        return null;
    //    }
    //
    //    /**
    //     *
    //     * @setter inputStream
    //     * @return
    //     */
    //    public static byte[] readGzipFile(InputStream inputStream){
    //        try (GZIPInputStream is = new GZIPInputStream(inputStream)){
    //            ByteArrayBuilder builder = new ByteArrayBuilder(8192 * 1024);
    //
    //            byte abyte0[] = new byte[2048 * 1024];
    //            int length = 0;
    //            while ((length = is.read(abyte0)) != -1){
    //                builder.append(Arrays.copyOf(abyte0, length));
    //            }
    //            return builder.toByteArray();
    //        } catch (IOException ioexception){
    //            throw new RuntimeException(ioexception);
    //        }
    //    }
    //
    //    public static byte[] loadFileFromClassPath(String sourcePath)
    //            throws IOException{
    //        try (InputStream is = ClassLoader.getSystemResourceAsStream(sourcePath)){
    //            if (is == null){
    //                return null;
    //            }
    //
    //            return ByteStreams.toByteArray(is);
    //        }
    //    }
    //
    //    /**
    //     * 从ClassLoader查找指定资源文件
    //     * @setter sourcePath  资源路径
    //     * @return byte[]
    //     * @throws IOException
    //     */
    //    public static InputStream getInputStreamFromClassPath(String sourcePath)
    //            throws IOException{
    //        assert sourcePath != null && sourcePath.length() > 0;
    //        return ClassLoader.getSystemResourceAsStream(sourcePath);
    //    }

    public static String stripSuffix(String input) {
        int pos = input.lastIndexOf(".");
        if (pos < 0) {
            return input;
        }
        if (pos == 0) {
            return "";
        }
        return input.substring(0, pos);
    }

    public static String[] split(String s, String separator) {
        return StringUtils.splitByWholeSeparatorPreserveAllTokens(s, separator);
    }


    private static final byte[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',};

    public static final byte[] asHex(byte[] input) {
        int length = input.length;
        byte[] result = new byte[length * 2];
        for (int i = 0, x = 0; i < length; i++) {
            result[x++] = HEX_CHARS[(input[i] >>> 4) & 0xf];
            result[x++] = HEX_CHARS[input[i] & 0xf];
        }
        return result;
    }

    private static final byte[] UPPER_HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',};

    public static final byte[] asUpperHex(byte[] input) {
        int length = input.length;
        byte[] result = new byte[length * 2];
        for (int i = 0, x = 0; i < length; i++) {
            result[x++] = UPPER_HEX_CHARS[(input[i] >>> 4) & 0xf];
            result[x++] = UPPER_HEX_CHARS[input[i] & 0xf];
        }
        return result;
    }

    public static String json(Object object) {
        return JSON.toJSONString(object);
    }

    public static byte[] zlibCompress(byte[] input) {
        int maxSize = ((int) Math.ceil(input.length * 1.001)) + 44;
        byte[] output = new byte[maxSize];
        Deflater de = new Deflater(Deflater.BEST_COMPRESSION);
        de.setInput(input);
        de.finish();
        int size = de.deflate(output);
        return Arrays.copyOf(output, size);
    }

 
    /**
     * 上次午夜时间点
     */
    public static long getLastMidnight(long ctime) {
        DateTime now = new DateTime(ctime);
        return new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).getMillis();
    }

    /**
     * 下次午夜时间点
     */
    public static long getNextMidnight(long ctime) {
        return getLastMidnight(ctime) + DateTimeConstants.MILLIS_PER_DAY;
    }

    /**
     * 计算跟今天0点的时间差，最大是0，基本上是负数
     *
     * @param ctime
     * @return
     */
    public static long calculateMillisToTodayStart(long ctime) {
        DateTime now = new DateTime(ctime);
        long lastMidnight = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).getMillis();
        return lastMidnight - now.getMillis();
    }

    public static long calculateMillisToMidnight(long ctime) {
        DateTime now = new DateTime(ctime);
        long lastMidnight = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0).getMillis();
        return lastMidnight + DateTimeConstants.MILLIS_PER_DAY - now.getMillis();
    }

    public static long calculateMillisToNextHour(long ctime) {
        DateTime now = new DateTime(ctime);
        long lastHour = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), now.getHourOfDay(), 0).getMillis();
        return lastHour + DateTimeConstants.MILLIS_PER_HOUR - now.getMillis();
    }

    public static long calculateDelay(int initialHour, int initialMinute, long ctime) {
        DateTime cdt = new DateTime(ctime);
        DateTime todayTarget = cdt.withTime(initialHour, initialMinute, 0, 0);
        if (todayTarget.isBefore(ctime)) {
            todayTarget = todayTarget.plusDays(1);
        }

        return todayTarget.getMillis() - ctime;
    }

 
    /**
     * 返回数字最小的index, 有相同数字的话, 优先返回前面的
     *
     * @param array
     * @return
     */
    public static int getMinIndex(int[] array) {
        assert array.length > 0;
        int min = array[0];
        int result = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                result = i;
            }
        }
        return result;
    }

    public static int divide(int count, int maxCount) {
        return (count + maxCount - 1) / maxCount;
    }

    public static int divide(long count, long maxCount) {
        return (int) ((count + maxCount - 1) / maxCount);
    }

    public static int square(int i) {
        return i * i;
    }

    public static int getStringLength(byte[] b_name) {
        int len = 0; //定义返回的字符串长度
        int j = 0;
        int limit = b_name.length - 1;
        while (j <= limit) {
            short tmpst = (short) (b_name[j] & 0xF0);
            if (tmpst >= 0xB0) {
                if (tmpst < 0xC0) {
                    j += 2;
                    len += 2;
                } else if ((tmpst == 0xC0) || (tmpst == 0xD0)) {
                    j += 2;
                    len += 2;
                } else if (tmpst == 0xE0) {
                    j += 3;
                    len += 2;
                } else if (tmpst == 0xF0) {
                    short tmpst0 = (short) ((b_name[j]) & 0x0F);
                    if (tmpst0 == 0) {
                        j += 4;
                        len += 2;
                    } else if ((tmpst0 > 0) && (tmpst0 < 12)) {
                        j += 5;
                        len += 2;
                    } else if (tmpst0 > 11) {
                        j += 6;
                        len += 2;
                    }
                }
            } else {
                j += 1;
                len += 1;
            }
        }
        return len;
    }

  
    public static boolean hasDuplicate(Object[] array) {
        int len = array.length;
        for (int i = 0; i < len; i++) {
            Object s = array[i];
            for (int j = i + 1; j < len; j++) {
                if (array[j].equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasDuplicate(String[] array) {
        int len = array.length;
        for (int i = 0; i < len; i++) {
            String s = array[i];
            for (int j = i + 1; j < len; j++) {
                if (array[j].equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasDuplicate(int[] array) {
        int len = array.length;
        for (int i = 0; i < len; i++) {
            int s = array[i];
            for (int j = i + 1; j < len; j++) {
                if (array[j] == s) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasDuplicate(long[] array) {
        int len = array.length;
        for (int i = 0; i < len; i++) {
            long s = array[i];
            for (int j = i + 1; j < len; j++) {
                if (array[j] == s) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAsc(int[] array) {
        for (int idx = 0; idx < array.length - 1; idx++) {
            if (array[idx] > array[idx + 1]) {
                return false;
            }
        }

        return true;
    }

    public static <V> V getSafeObject(V[] array, int idx) {
        if (idx >= 0 && idx < array.length) {
            return array[idx];
        }

        return null;
    }

    public static <V> V getValidObject(V[] array, int idx) {
        if (idx < 0) {
            return array[0];
        }

        if (idx >= array.length) {
            return array[array.length - 1];
        }

        return array[idx];
    }

    public static int getValidInteger(int[] array, int idx) {
        if (idx < 0) {
            return array[0];
        }

        if (idx >= array.length) {
            return array[array.length - 1];
        }

        return array[idx];
    }

    public static long getValidLong(long[] array, int idx) {
        if (idx < 0) {
            return array[0];
        }

        if (idx >= array.length) {
            return array[array.length - 1];
        }

        return array[idx];
    }

    public static float getValidFloat(float[] array, int idx) {
        if (idx < 0) {
            return array[0];
        }

        if (idx >= array.length) {
            return array[array.length - 1];
        }

        return array[idx];
    }

    public static boolean getValidBoolean(boolean[] array, int idx) {
        if (idx < 0) {
            return array[0];
        }

        if (idx >= array.length) {
            return array[array.length - 1];
        }

        return array[idx];
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

    public static int parseInt(Object master, String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(master + " 解析数值时出错, " + str);
        }
    }

  

    public static int getAngleBetween(int d1, int d2) {
        return getInferiorAngle(d1 - d2);
    }

    /**
     * 劣角（小于180度的角）
     * @param degree
     * @return
     */
    private static int getInferiorAngle(int degree) {
        int t = Math.abs(degree);
        if (t > 180) {
            return 360 - t;
        } else {
            return t;
        }
    }

    public static int getDirection(int baseX, int baseY, int targetX, int targetY, int lastDirection) {

        if (baseX == targetX && baseY == targetY) {
            return lastDirection;
        }

        int degree = (int) Math.toDegrees(Math.atan2(baseY - targetY, targetX - baseX));

        if (degree < 0) {
            degree = 360 - ((-degree) % 360); // 转成正角度
        }

        if (degree >= 360) {
            degree %= 360; // 防止超出
        }

        return degree;  // flash 的坐标的y是反的
    }

 
    

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Throwable e) {
            logger.error("Error closing closable", e);
        }
    }

    public static String utf8ToUnicode(String inStr) {
        /**
         * java 里面的字符编码就是unicode,把字符用16进制输出即可
         * java 里面的一个char 用两个字节表示
         */
        if (inStr == null || inStr.isEmpty()) {
            return inStr;
        }
        char[] charBuffer = inStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(charBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(charBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = charBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                //汉字
                short bit16_13 = (short) ((charBuffer[i] & 0xF000) >> 12);
                String hexS1 = Integer.toHexString(bit16_13);

                short bit12_9 = (short) ((charBuffer[i] & 0x0F00) >> 8);
                String hexS2 = Integer.toHexString(bit12_9).toLowerCase();

                short bit8_5 = (short) ((charBuffer[i] & 0x00F0) >> 4);
                String hexS3 = Integer.toHexString(bit8_5);

                short bit4_1 = (short) (charBuffer[i] & 0x000F);
                String hexS4 = Integer.toHexString(bit4_1);

                sb.append("\\u").append(hexS1).append(hexS2).append(hexS3).append(hexS4);
            }
        }
        return sb.toString();
    }

    public static int[] str2intArray(String str, String separator) {
        String[] strArray = StringUtils.split(str, separator);
        if (strArray == null || strArray.length == 0) {
            return Empty.INT_ARRAY;
        }

        int[] array = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            array[i] = Integer.parseInt(strArray[i]);
        }

        return array;
    }

    public static long[] str2longArray(String str, String separator) {
        String[] strArray = StringUtils.split(str, separator);
        if (strArray == null || strArray.length == 0) {
            return Empty.LONG_ARRAY;
        }

        long[] array = new long[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            array[i] = Long.parseLong(strArray[i]);
        }

        return array;
    }
 

    public static int max(int... values) {
        int maxValue = Integer.MIN_VALUE;

        for (int value : values) {
            maxValue = Math.max(value, maxValue);
        }

        return maxValue;
    }

 
    public static int min(int... values) {
        int minValue = Integer.MAX_VALUE;

        for (int value : values) {
            minValue = Math.min(value, minValue);
        }

        return minValue;
    }

 

    public static long max(long... values) {
        assert values.length > 0;

        long max = Long.MIN_VALUE;
        for (long value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static long min(long... values) {
        assert values.length > 0;

        long min = Long.MAX_VALUE;
        for (long value : values) {
            min = Math.min(min, value);
        }
        return min;
    }

    /**
     * 二舍三入，和七舍八入，取整五整十的等级；
     * @param value
     * @return
     */
    public static int getLevelWith5(int value) {
        return (int) Math.round((float) value / 5) * 5;
    }

    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static <T extends Enum<T>> T parseEnum(Class<T> cls, String name) {
        checkArgument(cls.isEnum(), "传进来的类必须是枚举!{}, {}", cls, name);

        for (Enum<T> e : cls.getEnumConstants()) {
            if (name.equalsIgnoreCase(e.name())) {
                return (T) e;
            }
        }

        return null;
    }

    public static Method getDeclaredMethodOrNull(Class<?> cls, String name, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
 

    public static HashMap<String, String> getKV(String str) {
        HashMap<String, String> map = Maps.newHashMap();
        try {
            String[] kvStr = str.split("\\;");
            for (String s : kvStr) {
                map.put(s.substring(0, s.indexOf("=")), s.substring(s.indexOf("=") + 1));
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(str);
            return map;
        }
    }

    public static List<HashMap<String, String>> getKVs(String str) {
        List<HashMap<String, String>> list = Lists.newArrayListWithCapacity(1);
        if (Strings.isNullOrEmpty(str)) {
            return list;
        }
        for (String s : str.split("\\$")) {
            list.add(getKV(s));

        }
        return list;
    }


    public static String fillSpaceStr(String s, int len) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < len) {
            sBuilder.append(" ");
        }
        s = sBuilder.toString();
        return s;
    }

    /**
     * 10以内的数字转中文
     * @param num
     * @return
     */
    public static String singleNumberToCn(int num) {
        return nums[num];
    }

    /**
     * 获取异常的调用堆栈信息。
     *
     * @return 调用堆栈
     */
    public static String toStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e1) {
            return "";
        }
    }

    //
    //    public static void printBlock(BlockInfo blockInfo, String where) {
    //        int iLen = blockInfo.numBlocksX;
    //        int jLen = blockInfo.numBlocksY;
    //
    //
    //        StringBuilder rowStr = new StringBuilder();
    //        for (int j = 0; j < jLen; j++) {
    //            rowStr.append("\n ");
    //            for (int i = 0; i < iLen; i++) {
    //                rowStr.append(blockInfo.isWalkable(i, j) ? 1 : 0);
    //            }
    //        }
    //        logger.debug("where:{} info:{}", where, rowStr.toString());
    //    }
    public static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String joinStrArr(String[] arr, String split) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String i : arr) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(i);
            } else {
                stringBuilder.append(split);
                stringBuilder.append(i);
            }
        }
        return stringBuilder.toString();
    }

    public static String joinIntArr(int[] arr, String split) {
        StringBuilder stringBuilder = new StringBuilder();
        for (long i : arr) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(i);
            } else {
                stringBuilder.append(split);
                stringBuilder.append(i);
            }
        }
        return stringBuilder.toString();
    }
}

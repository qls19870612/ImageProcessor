package com.ejjiu.image.utils;

import com.google.common.math.LongMath;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * @author Liwei
 *
 */
public class TimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);
    public static final DateTimeFormatter FORMATTER1 = DateTimeFormat.forPattern("yyMMddHHmmss");
    public static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yy-MM-dd-HH-mm-ss");
    public static final DateTimeFormatter FORMATTER2 = DateTimeFormat.forPattern("yy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yy年MM月dd日");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MM月dd日 HH:mm:ss");

    private static final ISOChronology chronology = ISOChronology.getInstance();

    public static final long MILLIS_PER_DAY = DateTimeConstants.MILLIS_PER_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_PER_DAY && interval > -1L * MILLIS_PER_DAY && toDay(ms1) == toDay(ms2);
    }

    public static String printTime(long time) {
        return FORMATTER.print(time);
    }

    public static String printTime(int time) {
        return FORMATTER.print(TimeUtils.secondToMills(time));
    }

    /**
     * MM月dd日 HH:mm:ss
     * @param time
     * @return
     */
    public static String printDateTime(int time) {
        return DATE_TIME_FORMATTER.print(TimeUtils.secondToMills(time));
    }

    public static String printTime2(long time) {
        return FORMATTER2.print(time);
    }

    public static String printDate(long time) {
        return DATE_FORMATTER.print(time);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_PER_DAY;
    }

    //    public static long getMillisOfDayStart(long millis) {
    //        return millis + TimeUtils.MILLIS_PER_DAY - TimeUtil.computeStartOfNextDay(millis);
    //    }

    public static int toHourOfDay(long instant) {
        return chronology.hourOfDay().get(instant);
    }

    /**
     * 返回多少号，取值[1-31]
     * @param instant
     * @return
     */
    public static int toDayOfMonth(long instant) {
        return chronology.dayOfMonth().get(instant);
    }

    /**
     * 返回多少号，取值[1-12]
     * @param instant
     * @return
     */
    public static int toMonthOfYear(long instant) {
        return chronology.monthOfYear().get(instant);
    }

    public static int toYear(long instant) {
        return chronology.year().get(instant);
    }

    /**
     * 判断某一个操作，自上次执行的时间点lastOpTime之后，当前时间点currentTime经过了 多少次重置时间点resetTime
     *
     * <p>
     */
    public static int getDoDailyResetCount(long currentTime, long lastOpTime, LocalTime resetTime) {
        if (currentTime <= lastOpTime) {
            return 0;
        }

        long resetMillisOfDay = resetTime.getMillisOfDay();
        return Days.daysBetween(new LocalDate(lastOpTime - resetMillisOfDay), new LocalDate(currentTime - resetMillisOfDay)).getDays();
    }

    public static int getDoDailyResetCount(long currentTime, long lastOpTime) {
        return getDoDailyResetCount(currentTime, lastOpTime, LocalTime.MIDNIGHT);
    }

    /**
     * 从startTime时间开始，找到endTime时间，一共经过了多少次activityTimeData配置的活动时间 找的次数最多不能超过10次
     * @param startTime
     * @param endTime
     * @param activityTimeData
     * @param maxFindTimes 设置一个最大值，防止找下一活动时间过长而消耗内存（找那多次也没有意义)
     * @return
     */
    public static int findActivityPastTimes(long startTime, long endTime, TimeData activityTimeData, int maxFindTimes) {
        startTime += TimeUnit.MINUTES.toMillis(1);
        //        logger.debug("findActivityPastTimes printTime(startTime):{}", printTime(startTime));
        //        logger.debug("findActivityPastTimes printTime(endTime):{}", printTime(endTime));
        //        logger.debug("findActivityPastTimes activityTimeData:{}", activityTimeData.input);
        if (endTime <= startTime) {
            return 0;
        }
        if (maxFindTimes > 10) {
            throw new RuntimeException("不用找通过活动 这么多次吧");

        }
        int retTimes = 0;
        while (retTimes < maxFindTimes) {
            DurationTime nextTime = activityTimeData.getNextTime(startTime);
            if (nextTime.startTime >= endTime) {
                break;
            }
            startTime = nextTime.startTime + TimeUnit.MINUTES.toMillis(1);
            String s = printTime(startTime);
            //            logger.debug("findActivityPastTimes s:{}", s);
            retTimes++;
        }
        //        logger.debug("findActivityPastTimes retTimes:{}", retTimes);
        return retTimes;
    }

    /**
     * 传入 23:00 格式数据返回 相对于零点的分钟数
     */
    public static int getMinuteOfDay(String timeStr) {
        try {
            int hour, min;

            String[] time = timeStr.split(":");
            if (time.length != 2) {
                throw new IllegalArgumentException("getMinuteOfDay时, 参数格式错误:" + timeStr);
            }

            hour = Integer.parseInt(time[0]);
            min = Integer.parseInt(time[1]);

            if (hour < 0 || hour >= 24) {
                throw new IllegalArgumentException("getMinuteOfDay时, 小时参数格式错误:" + time[0]);
            }
            if (min < 0 || min >= 60) {
                throw new IllegalArgumentException("getMinuteOfDay时, 分钟参数格式错误:" + time[1]);
            }

            return hour * 60 + min;
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

    public static int millsToSecond(long mills) {
        return (int) LongMath.divide(mills, 1000, RoundingMode.FLOOR);
    }

    public static long secondToMills(int mills) {
        return (mills * 1000L);
    }


    public static String printTimeDuration(long mills) {
        int second = millsToSecond(mills);
        int remainSecond = second % 60;
        int minute = second / 60;
        int remainMinute = minute % 60;

        int hour = minute / 60;
        int remainHour = hour % 24;
        int day = hour / 24;
        StringBuilder stringBuilder = new StringBuilder();
        if (day > 0) {
            stringBuilder.append(day);
            stringBuilder.append("天");
        }
        if (stringBuilder.length() > 0 || remainHour > 0) {
            stringBuilder.append(remainHour);
            stringBuilder.append("小时");
        }
        if (stringBuilder.length() > 0 || remainMinute > 0) {
            stringBuilder.append(remainMinute);
            stringBuilder.append("分钟");
        }

        stringBuilder.append(remainSecond);
        stringBuilder.append("秒");
        return stringBuilder.toString();

    }

    private TimeUtils() {
    }

    public static int getSystemSecond() {

        return TimeUtils.millsToSecond(System.currentTimeMillis());
    }
}

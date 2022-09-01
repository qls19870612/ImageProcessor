package com.ejjiu.image.utils;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 活动时间解析类，用于解析类似[*][12][w5, w3][10:00, 12:00]的时间格式
 * @author 康露
 * 2014年7月7日
 */
public class TimeDataAs implements ITimeData {
    private static final Logger logger = LoggerFactory.getLogger(TimeDataAs.class);
    /** 合法的年份 **/
    private int[] arrYear = null;
    /** 合法的月份，月份从0开始 **/
    private int[] arrMonth = null;
    /** 合法的日期 **/
    private int[] arrDate = null;
    /** 合法的星期 **/
    private int[] arrDay = null;
    /**星期中文*/
    private String[] arrDayCN = null;
    /** 合法的时间 **/
    private DurationTimeConfig[] arrTime = null;
    /**用于描述文字的月份，月份从1开始。*/
    private int[] describeMonths = null;
    /**根据配置的字符串解析出的，对时间的文字描述*/
    private String timeDescribe;
    private String strTime;
    private ThreadLocal<CurrentTimeParam> currentTimeParamThreadLocal = ThreadLocal.withInitial(CurrentTimeParam::new);

    public String getStrTime() {
        return strTime;
    }

    private static int[] maxDayOfMonth = new int[]{31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


    private int getMaxDate(Date curTime) {
        int month = curTime.getMonth();
        if (month == 1) {
            if (isRunNian(curTime.getYear())) {
                return 29;
            }
            return 28;
        }

        return maxDayOfMonth[month];
    }

    private boolean isRunNian(int year) {

        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0 && year % 3200 != 0) || (year % 172800 == 0);
    }

    /**
     * 构造函数，传入需要被解析的时间描述字符串
     * @param strTime 需要被解析的时间描述字符串，例如[*][12][w5, w3][10:00, 12:00]
     * @author 康露 2014年7月7日
     */
    public TimeDataAs(String strTime) {
        if (StringUtils.isEmpty(strTime)) {
            return;
        }
        this.strTime = strTime;
        // 作弊，为了方便解析偷懒
        strTime = "]" + strTime.replaceAll(" ", "") + "[";
        String[] arrTime = strTime.split("]\\[");
        String strYear = arrTime[1];    // 年
        String strMonth = arrTime[2];    // 月
        String strDay = null;            // 日
        String strDate = null;            // 星期
        String str = arrTime[3];
        if (!str.contains("w")) {
            strDate = str;
        } else {
            strDay = str;
        }
        String strHour = arrTime[4];    // 小时:分钟
        parseYear(strYear);
        parseMonth(strMonth);
        parseDate(strDate);
        parseDay(strDay);
        parseTime(strHour);
    }

    /** 年份 **/
    private void parseYear(String str)

    {
        if (str.contains("*")) {// 所有的年份都ok
            return;
        }
        arrYear = Utils.str2intArray(str, ",");
        Arrays.sort(arrYear);
    }

    /** 月份 **/
    private void parseMonth(String str)

    {
        if (str.contains("*")) {// 所有的月份都ok
            return;
        }

        describeMonths = Utils.str2intArray(str, ",");
        Arrays.sort(describeMonths);
        arrMonth = new int[describeMonths.length];
        int index = 0;
        for (int i : arrMonth) {
            describeMonths[index++] = i;
        }


    }

    /** 星期 **/
    private void parseDay(String str)

    {
        if (isEmpty(str) || str.contains("*")) {// 所有的星期都ok
            return;
        }

        str = str.replaceAll("w", "");
        int[] arr = Utils.str2intArray(str, ",");
        arrDayCN = new String[arr.length];
        arrDay = new int[arr.length];
        int index = 0;
        for (int i : arr) {
            i = i == 7 ? 0 : i;
            arrDay[index++] = i;
        }
        Arrays.sort(arrDay);
        index = 0;
        for (int i : arrYear) {
            arrDayCN[index++] = TextUtil.getCNNumber(i);
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /** 日期 **/
    private void parseDate(String str)

    {
        if (isEmpty(str) || str.contains("*")) {// 所有的星期都ok
            return;
        }

        arrDate = Utils.str2intArray(str, ",");
        Arrays.sort(arrDate);
    }

    /** 时间 **/
    private void parseTime(String str)

    {
        if (str.contains("*")) {// 所有的日期都ok
            return;
        }
        String[] arr = str.split(",");
        arrTime = new DurationTimeConfig[arr.length];

        int index = 0;
        DurationTimeConfig prevConfig = null;
        for (String s : arr) {
            String[] split = s.split("-");
            String startTime = split[0];

            int[] startTimes = Utils.str2intArray(startTime, ":");
            int startHour = startTimes[0];
            int startMin = startTimes[1];

            int endHour;
            int endMin;

            if (split.length > 1) {
                String endTime = split[1];
                int[] endTimes = Utils.str2intArray(endTime, ":");
                endHour = endTimes[0];
                endMin = endTimes[1];
            } else {
                endHour = startHour;
                endMin = startMin;
            }

            DurationTimeConfig timeConfig = new DurationTimeConfig(startHour, startMin, endHour, endMin);
            if (timeConfig.durationMs < 0) {
                throw new RuntimeException("持续时间不能小于0,getStrTime:" + getStrTime());
            }
            arrTime[index++] = timeConfig;
            if (prevConfig != null && timeConfig.start < prevConfig.end) {
                throw new RuntimeException("时间格式配置错误,必需按照顺序配置，并不能重叠 strTime:" + strTime);
            }
            prevConfig = timeConfig;
        }

    }


    private boolean checkMonth(Date curTime) {
        if (null == arrMonth) {
            return true;
        }
        int month = curTime.getMonth();
        for (int i : arrMonth) {
            if (month == i) {
                return true;
            }
        }
        return false;
    }

    private CurrentTimeParam getTimeWithDate(CurrentTimeParam param) {
        Date curTime = param.getDate();
        int shortTimeMs = getShortTimeMs(curTime.getHours(), curTime.getMinutes(), curTime.getSeconds());

        if (null == arrTime) {
            return param;
        }

        for (DurationTimeConfig config : arrTime) {
            if (config.start >= shortTimeMs) {
                curTime.setHours(config.startHour);
                curTime.setMinutes(config.startMin);
                curTime.setSeconds(0);
                param.setConfig(config);
                return param;
            }
        }

        return null;
    }


    private int getLastMonthMaxDate(Date curTime)

    {
        Date date = new Date(curTime.getYear(), curTime.getMonth(), 1);
        date.setTime(date.getTime() - 10);
        return date.getDate();
    }

    private boolean checkDay(Date curTime)

    {
        if (null == arrDay) {
            return true;
        }

        for (int i : arrDay) {
            if (curTime.getDay() == i) {
                return true;
            }
        }
        return false;
    }

    private CurrentTimeParam getTimeWithMonth(CurrentTimeParam param)

    {
        Date curTime = param.getDate();
        if (!checkMonth(curTime)) {
            return null;
        }

        int idx = 0;
        CurrentTimeParam timeParam = null;
        if (null != arrDate) {
            for (idx = 0; idx < arrDate.length; idx++) {
                if (arrDate[idx] < curTime.getDate()) {
                    cleanHour(curTime);
                    continue;
                }
                if (curTime.getDate() != arrDate[idx]) {
                    cleanHour(curTime);
                    curTime.setDate(arrDate[idx]);
                }
                timeParam = getTimeWithDate(param);
                if (null != timeParam) {
                    return timeParam;
                }
                cleanHour(curTime);
            }
        } else if (null != arrDay) {
            for (idx = curTime.getDate(); idx <= getMaxDate(curTime); idx++) {
                curTime.setDate(idx);
                if (!checkDay(curTime)) {
                    cleanHour(curTime);
                    continue;
                }
                timeParam = getTimeWithDate(param);
                if (null != timeParam) {
                    return timeParam;
                }
                cleanHour(curTime);

            }
        } else {
            for (idx = curTime.getDate(); idx <= getMaxDate(curTime); idx++) {
                curTime.setDate(idx);
                timeParam = getTimeWithDate(param);
                if (null != timeParam) {
                    return timeParam;
                }
                cleanHour(curTime);
            }
        }
        return null;
    }

    private boolean checkYear(CurrentTimeParam param) {
        if (null == arrYear) {
            return true;
        }
        int year = param.getDate().getYear();
        for (int i : arrYear) {
            if (i == year) {
                return true;
            }
        }


        return false;
    }

    private CurrentTimeParam getTimeWithYear(CurrentTimeParam param) {

        if (!checkYear(param)) {
            return null;
        }

        int idx = 0;
        CurrentTimeParam timeParam = null;
        Date curTime = param.getDate();
        if (null == arrMonth) {
            for (idx = curTime.getMonth(); idx < 12; idx++) {
                curTime.setMonth(idx);
                timeParam = getTimeWithMonth(param);
                if (null != timeParam) {
                    return timeParam;
                }
                curTime.setDate(getFirstDate());
                cleanHour(curTime);
            }
        } else {
            for (int month : arrMonth) {
                if (month < curTime.getMonth()) {
                    curTime.setDate(getFirstDate());
                    cleanHour(curTime);
                    continue;
                }
                curTime.setMonth(month);
                timeParam = getTimeWithMonth(param);
                if (null != timeParam) {
                    return timeParam;
                }
                curTime.setDate(getFirstDate());
                cleanHour(curTime);
            }

        }
        return null;
    }

    private CurrentTimeParam getPrevTimeWithYear(CurrentTimeParam param) {
        if (!checkYear(param)) {
            return null;
        }
        int idx = 0;
        CurrentTimeParam retTime = null;
        int lastDate;
        Date curTime = param.getDate();
        if (null == arrMonth) {
            for (idx = curTime.getMonth(); idx >= 0; idx--) {
                //					trace(curTime.month,"curTime.month->TimeData.getPrevTimeWithYear()");
                if (curTime.getMonth() != idx) {
                    lastDate = getLastDate(curTime);
                    curTime.setMonth(idx);
                    curTime.setDate(lastDate);
                }
                //					trace(curTime,"curTime->TimeData.getPrevTimeWithYear()");
                retTime = getPrevTimeWithMonth(param);
                if (retTime != null) {
                    return retTime;
                }


                setHour(curTime);
            }
        } else {
            for (idx = arrMonth.length - 1; idx >= 0; idx--) {
                if (arrMonth[idx] > curTime.getMonth()) {
                    //						curTime.date = getLastDate(curTime);
                    //						cleanHour(curTime);
                    continue;
                }
                if (curTime.getMonth() != arrMonth[idx]) {
                    lastDate = getLastDate(curTime);
                    curTime.setMonth(arrMonth[idx]);
                    curTime.setDate(lastDate);
                }
                retTime = getPrevTimeWithMonth(param);
                if (retTime != null) {
                    return retTime;
                }
                setHour(curTime);
            }
        }

        return null;
    }

    private void setHour(Date curTime)

    {
        curTime.setHours(23);
        curTime.setMinutes(59);
        curTime.setSeconds(59);
        //        curTime.milliseconds = 999;
        //        curTime.setTime(curTime.getTime() + 999);
    }

    private CurrentTimeParam getPrevTimeWithMonth(CurrentTimeParam param)

    {
        if (null == arrTime) {
            return param;
        }
        int idx = 0;
        CurrentTimeParam retTime = null;
        Date curTime = param.getDate();
        if (arrDate != null) {
            for (idx = arrDate.length - 1; idx >= 0; idx--) {
                if (arrDate[idx] > curTime.getDate()) {
                    //						setHour(curTime);
                    continue;
                }
                if (curTime.getDate() != arrDate[idx]) {
                    //						setHour(curTime);
                    curTime.setDate(arrDate[idx]);

                }
                retTime = getPrevTimeWithDate(param);
                if (retTime != null) {
                    return retTime;
                }
                setHour(curTime);
            }
        } else if (arrDay != null) {
            for (idx = curTime.getDate(); idx > 0; idx--) {
                curTime.setDate(idx);
                if (!checkDay(curTime)) {
                    setHour(curTime);
                    continue;
                }
                retTime = getPrevTimeWithDate(param);
                if (retTime != null) {
                    return retTime;
                }
                setHour(curTime);

            }
        } else {
            for (idx = curTime.getDate(); idx > 0; idx--) {
                curTime.setDate(idx);
                retTime = getPrevTimeWithDate(param);
                if (retTime != null) {
                    return retTime;
                }
                setHour(curTime);
            }
        }

        return null;
    }

    private CurrentTimeParam getPrevTimeWithDate(CurrentTimeParam param)

    {
        Date curTime = param.getDate();
        int curHour = getShortTimeMs(curTime);
        for (int idx = arrTime.length - 1; idx >= 0; idx--) {
            DurationTimeConfig timeConfig = arrTime[idx];
            if (timeConfig.start < curHour) {
                curTime.setHours(timeConfig.startHour);
                curTime.setMinutes(timeConfig.startMin);
                curTime.setSeconds(0);
                param.setConfig(timeConfig);
                return param;
            }
        }
        return null;
    }

    private int getShortTimeMs(Date curTime) {
        int hours = curTime.getHours();
        int minutes = curTime.getMinutes();
        int seconds = curTime.getSeconds();
        return getShortTimeMs(hours, minutes, seconds);
    }

    private int getShortTimeMs(int hours, int minutes, int seconds) {
        return (hours * 3600 + minutes * 60 + seconds) * 1000;
        //        return (hours << 24) | (minutes << 16) | seconds;
    }

    private int getNextYear(Date curTime)

    {
        int year = curTime.getYear();
        if (null == arrYear) {
            return year + 1;
        }
        for (int i : arrYear) {
            if (i >= year) {
                return i;
            }
        }
        return 0;
    }

    private int getFirstMonth()

    {
        if (null == arrMonth) {
            return 0;
        }
        return arrMonth[0];
    }

    private int getFirstDate()

    {
        if (null == arrDate) {
            return 1;
        }
        return arrDate[0];
    }

    private void cleanHour(Date curTime)

    {
        curTime.setHours(0);
        curTime.setMinutes(0);
        curTime.setSeconds(0);
        //        curTime.milliseconds = 0;
    }

    public static String formatTimeToSpecString(int hour, int minute) {
        StringBuilder builder = new StringBuilder(5);
        if (hour > 9) {
            builder.append(hour);
        } else {
            builder.append(0);
            builder.append(hour);
        }
        builder.append(":");
        if (minute > 9) {
            builder.append(minute);
        } else {
            builder.append(0);
            builder.append(minute);
        }
        return builder.toString();
    }

    /**
     * 根据配置得到时间描述文字<br/>
     * 可以识别的例子<br/>
     * 以及转为文字的表现形式(一行是一个例子)： <br/>
     * 2011年、2012年 5月、8月 每周五 08:00, 15:30		<br/>
     * 每天 20:00<br/>
     * 每周一、周五 21:00, 22:00	<br/>
     * 5月 8日 20:00 	<br/>
     * 5月、6月 8日、10日 20:00, 21:00
     * @author zhengyang
     *  */
    public String getTimeDescribe()

    {
        if (timeDescribe != null) {
            return timeDescribe;
        }

        String yearStr = "";
        String monthStr = "";
        String dayStr = "";
        String timeStr = "";

        if (arrYear != null) {
            yearStr = Utils.joinIntArr(arrYear, "年、") + "年 ";

        }

        if (arrMonth != null) {
            monthStr = Utils.joinIntArr(describeMonths, "月、") + "月 ";
        }

        if (arrDay != null) {
            dayStr = getWeekDayString();
        } else if (arrDate != null) {
            dayStr = getDayString();
        } else {
            dayStr = "每天 ";
        }

        if (arrTime != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (DurationTimeConfig i : arrTime) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(formatTimeToSpecString(i.startHour, i.startMin));

            }
            timeStr = stringBuilder.toString();
        }


        timeDescribe = yearStr + monthStr + dayStr + timeStr;
        return timeDescribe;
    }


    /**
     * 时间描述，与getTimeDescribe不同在于时间显示为持续时间： <br/>
     * 每天 17:00-17:30 \n 每天 19:00-20:00		<br/>
     * @return
     *
     */
    public String getTimeFromToDescribe()

    {
        if (timeDescribe != null) {
            return timeDescribe;
        }

        String yearStr = "";
        String monthStr = "";
        String dayStr = "";
        String timeStr = "";

        if (arrYear != null) {
            yearStr = Utils.joinIntArr(arrYear, "年、") + "年 ";
        }

        if (arrMonth != null) {
            monthStr = Utils.joinIntArr(describeMonths, "月、") + "月 ";
        }

        if (arrDay != null) {
            dayStr = getWeekDayString();
        } else if (arrDate != null) {
            dayStr = getDayString();
        } else {
            dayStr = "每天 ";
        }

        if (arrTime != null) {

            StringBuilder stringBuilder = new StringBuilder();
            for (DurationTimeConfig i : arrTime) {
                String timeFrom = formatTimeToSpecString(i.startHour, i.startMin);
                String timeTo = formatTimeToSpecString(i.endHour, i.endMin);
                stringBuilder.append(yearStr);
                stringBuilder.append(monthStr);
                stringBuilder.append(dayStr);
                stringBuilder.append(" ");
                stringBuilder.append(timeFrom);
                stringBuilder.append("-");
                stringBuilder.append(timeTo);
                stringBuilder.append(",\n");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 2);
            }
            timeStr = stringBuilder.toString();
        }

        timeDescribe = timeStr;
        return timeDescribe;
    }

    /**一个月中的哪几天*/
    private String getDayString() {
        return Utils.joinIntArr(arrDate, "日、") + "日";
    }

    /**一个星期中的哪几天*/
    private String getWeekDayString()

    {
        return "周" + Utils.joinStrArr(arrDayCN, "、周");
    }

    @Override
    public DurationTime getNextTime(long curTime) {
        CurrentTimeParam currentTimeParam = currentTimeParamThreadLocal.get();

        currentTimeParam.initData(curTime);

        CurrentTimeParam nextDate = getNextDate(currentTimeParam);
        if (nextDate != null) {
            long startTime = nextDate.date.getTime();
            return new DurationTime(startTime, startTime + nextDate.config.durationMs);
        }
        return null;

    }


    /**
     * 获取下一个时间点
     * @return 返回下一个时间点
     * @author 康露 2014年7月7日
     */
    public CurrentTimeParam getNextDate(CurrentTimeParam param) {

        Date curTime = param.getDate();
        CurrentTimeParam timeParam = null;
        // 从今年开始搜，最多往后搜十年
        for (int idx = 0; idx < 10; idx++) {
            timeParam = getTimeWithYear(param);
            if (null != timeParam) {
                break;
            }

            curTime.setYear(getNextYear(curTime));
            curTime.setMonth(getFirstMonth());
            curTime.setDate(getFirstDate());
            cleanHour(curTime);
        }
        return timeParam;
    }

    @Override
    public DurationTime getBeforeTime(long curTime) {

        CurrentTimeParam param = currentTimeParamThreadLocal.get();
        param.initData(curTime);
        CurrentTimeParam beforeDate = getBeforeTime(param);
        if (beforeDate != null) {
            long startTime = param.getDate().getTime();
            return new DurationTime(startTime, startTime + param.config.durationMs);
        }
        return null;
    }

    /**
     *获取已经过去的时间点
     * @param param 从哪个时间点开始算起
     * @return
     * liangsong添加
     */
    public CurrentTimeParam getBeforeTime(CurrentTimeParam param) {
        CurrentTimeParam retTime;

        Date curTime = param.getDate();

        /**<向前搜索10年>*/
        for (int i = 0; i < 10; i++) {
            retTime = getPrevTimeWithYear(param);
            if (retTime != null) {
                return retTime;
            }
            curTime.setYear(getPrevYear(curTime));
            curTime.setMonth(getLastMonth());
            curTime.setDate(getLastDate(curTime));
            setHour(curTime);
        }

        return null;
    }

    private int getLastDate(Date curTime)

    {
        if (arrDate == null || arrDate.length == 0) {
            if (curTime.getDate() == 1) {
                /**<如果是一个月的第一天，月份要-1，所以要获取前一个月的总天数>*/
                return getLastMonthMaxDate(curTime);
            }
            return getMaxDate(curTime);
        }
        return arrDate[arrDate.length - 1];
    }


    private int getLastMonth()

    {
        if (null == arrMonth) {
            return 11;
        }
        return arrMonth[arrMonth.length - 1];
    }

    private int getPrevYear(Date curTime)

    {
        int year = curTime.getYear();
        if (null == arrYear) {
            return year - 1;
        }
        for (int idx = arrYear.length - 1; idx >= 0; idx--) {
            if (arrYear[idx] < year) {
                return arrYear[idx];
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return getStrTime();
    }


    private class DurationTimeConfig {
        public final int start;
        public final int end;
        public final long durationMs;
        public final int startHour;
        public final int startMin;
        public final int endHour;
        public final int endMin;


        public DurationTimeConfig(int startHour, int startMin, int endHour, int endMin) {
            this.start = getShortTimeMs(startHour, startMin, 0);
            this.end = getShortTimeMs(endHour, endMin, 0);
            this.startHour = startHour;
            this.startMin = startMin;
            this.endHour = endHour;
            this.endMin = endMin;
            this.durationMs = end - start;
        }
    }


    private class CurrentTimeParam {
        @Getter
        private Date date;
        @Getter
        @Setter
        public DurationTimeConfig config;

        public CurrentTimeParam() {
            date = new Date();
        }

        public void initData(long time) {
            this.date.setTime(time);
        }
    }


}


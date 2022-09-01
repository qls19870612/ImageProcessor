package com.ejjiu.image.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.ejjiu.image.collection.IntPair;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;



import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TimeData implements ITimeData {
    private static final Logger logger = LoggerFactory.getLogger(TimeData.class);
    private static final DurationTime MAX_LONG = new DurationTime(Long.MAX_VALUE, Long.MAX_VALUE);
    private static final DurationTime MIN_LONG = new DurationTime(0, 0);

 
    public static TimeData parse(String input) {
        checkNotNull(input);

        List<String> components = Lists.newArrayList();

        int startPos = -1;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '[') {
                checkArgument(startPos == -1, "时间格式不正确. 应该是 [2011][11][w1,w2,w5][16:00,18:05-20:05]: %s", input);
                startPos = i;
            } else if (c == ']') {
                checkArgument(startPos != -1, "时间格式不正确. 应该是 [2011][11][w1,w2,w5][16:00,18:05-20:05]: %s", input);
                components.add(input.substring(startPos + 1, i).trim());
                startPos = -1;
            }
        }
        checkArgument(startPos == -1 && components.size() == 4, "时间格式不正确. 应该是 [2011][11][w1,w2,w5][16:00,18:05-20:05]: %s", input);

        return parse(components.toArray(Empty.STRING_ARRAY), input);
    }

    private static TimeData parse(String[] args, String input) {
        assert args.length == 4;
        // 年
        String year = args[0];
        int yearLimit = 0;
        if ("*".equals(year)) {
        } else {
            yearLimit = Integer.parseInt(year);
        }

        // 月
        String month = args[1];
        Set<Integer> monthLimits = Sets.newHashSet();
        if ("*".equals(month)) {
        } else {
            String[] ms = month.split(",");
            for (String m : ms) {
                m = m.trim();
                int mon = Integer.parseInt(m);
                checkArgument(mon >= 1 && mon <= 12, "月份必须为1-12: %s: %s", mon, input);
                monthLimits.add(mon);
            }
            checkArgument(monthLimits.size() > 0, "月份必须填. 无月份限制填*: %s", input);
        }

        // 日
        String day = args[2];
        Set<Integer> dayLimits = Sets.newHashSet();
        Set<Integer> weekdayLimits = Sets.newHashSet();
        if ("*".equals(day)) {
        } else {
            String[] ds = day.split(",");
            for (String d : ds) {
                d = d.trim();
                if (d.startsWith("w")) {
                    int weekday = Integer.parseInt(d.substring(1));
                    checkArgument(weekday >= 1 && weekday <= 7, "星期几必须为1-7: %s: %s", d, input);
                    weekdayLimits.add(weekday);
                } else {
                    int dd = Integer.parseInt(d);
                    checkArgument(dd >= 1 && dd <= 31, "日期必须为1-31: %s: %s", d, input);
                    dayLimits.add(dd);
                }

                checkArgument(dayLimits.size() > 0 || weekdayLimits.size() > 0, "必须配置日期. 没有限制填*: %s", input);// 必须要有
                checkArgument(dayLimits.size() == 0 || weekdayLimits.size() == 0, "日期要么都是星期, 要么都是日期: %s", input); // 只能有一个
            }
        }

        // 时间
        String time = args[3];
        Set<IntPair> times = Sets.newHashSet();
        String[] ts = time.split(",");
        for (String t : ts) {
            t = t.trim();
            int startTime = 0;
            int endTime = 0;
            if (t.contains("-")) {
                String[] dts = t.split("-");
                checkArgument(dts.length == 2, "带结束时间的时间配置错误");
                startTime = getHourAndMinute(dts[0], input);
                endTime = getHourAndMinute(dts[1], input);
            } else {
                startTime = getHourAndMinute(t, input);
                endTime = startTime;
            }
            times.add(new IntPair(startTime, endTime));

        }

        checkArgument(times.size() > 0, "必须配置时间, 格式 hh:mm,hh:mm");

        return new TimeData(input, yearLimit, monthLimits, weekdayLimits, dayLimits, times);
    }

    private static int getHourAndMinute(String t, String input) {
        int lpos = t.indexOf(":");
        checkArgument(lpos > 0, "时间格式错误, 必须是hh:mm : %s", input);
        int hour = Integer.parseInt(t.substring(0, lpos));
        int minute = Integer.parseInt(t.substring(lpos + 1));

        checkArgument(hour >= 0 && hour <= 23, "小时必须是 0-23: %s", input);
        checkArgument(minute >= 0 && minute <= 59, "分钟必须是0-59: %s", input);
        return Utils.short2Int(hour, minute);
    }

    // -----------------------

    public final String input;

    private final int year;

    private final int[] month;

    private final int[] days;

    private final boolean isWeekday;

    private transient final boolean hasDayLimit;

    private transient final boolean hasMonthLimit;

    private final IntPair[] hours;

    private transient final boolean isDailyTime;

    private TimeData(String input, int year, Collection<Integer> months, Collection<Integer> weekdays, Collection<Integer> monthDay,
            Set<IntPair> hourMinutes) {
        this.input = input;
        this.year = year;

        this.month = toArray(months);
        this.hasMonthLimit = this.month.length > 0;

        if (weekdays.size() == 0) {
            if (monthDay.size() == 0) {
                hasDayLimit = false;
                days = Empty.INT_ARRAY;
                isWeekday = false;
            } else {
                days = toArray(monthDay);
                isWeekday = false;
                hasDayLimit = true;
            }
        } else {
            if (weekdays.size() == 7) {
                // 7天都写了, 其实是没有限制的
                hasDayLimit = false;
                days = Empty.INT_ARRAY;
                isWeekday = false;
            } else {
                days = toArray(weekdays);
                isWeekday = true;
                hasDayLimit = true;
            }
        }

        this.hours = toPairArray(hourMinutes);

        for (int i = 0; i < hours.length - 1; i++) {
            for (int j = i + 1; j < hours.length; j++) {
                checkArgument(!((hours[i].right <= hours[j].right && hours[i].right >= hours[j].left) ||
                        (hours[i].left <= hours[j].right && hours[i].left >= hours[j].left) ||
                        (hours[j].right <= hours[i].right && hours[j].right >= hours[i].left) ||
                        (hours[j].left <= hours[i].right && hours[j].left >= hours[i].left)), "时间配置%s 的时间段有重叠", input);
            }
        }

        // 检查, 不能是限制了月份, 又限制了日期, 但是是永远达不到的, 比如一定要2月30号. 或者一定要[2,4]的31号
        if (hasMonthLimit) {
            if (hasDayLimit) {
                if (!isWeekday) {
                    int firstDay = days[0];
                    // 来问题了
                    switch (firstDay) {
                        case 29: {
                            // 如果月份只有2, 则要么不限年, 要么这一年是有29号的
                            if (month.length == 1 && month[0] == 2) {
                                if (this.year != 0) {
                                    DateTime dt = new DateTime(this.year, 2, 1, 0, 0);
                                    int dayLimit = dt.dayOfMonth().getMaximumValue();
                                    checkArgument(dayLimit >= 29, "%s年 2月 没有 29号", this.year);
                                }
                            }
                            break;
                        }

                        case 30: {
                            // 月份不能只有2
                            checkArgument(month.length > 1 || month[0] != 2, "2月没有30号");
                            break;
                        }

                        case 31: {
                            // 月份不能只有 2, 4, 6, 9, 11
                            boolean valid = false;
                            for (int m : this.month) {
                                if (isMonthCanHave31Days(m)) {
                                    valid = true;
                                    break;
                                }
                            }
                            checkArgument(valid, "配置的月份都是没有31号的");
                            break;
                        }

                        default: {
                            break;
                        }
                    }
                }
            }
        }

        isDailyTime = year == 0 && !hasMonthLimit && !hasDayLimit && !isWeekday;
    }

    /**
     * 是否每天都有的，true表示是，false表示否
     * @return
     */
    public boolean isDailyTime() {
        return isDailyTime;
    }

    public IntPair[] getHours() {
        return hours;
    }

    private static boolean isMonthCanHave31Days(int month) {
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12;
    }

    /**
     * 得到下一个符合要求的时间点. 没有就返回0
     * @param from
     * @return
     */
    @Override
    public DurationTime getNextTime(long from) {
        DateTime fromDateTime = new DateTime(from);

        int currentYear = fromDateTime.getYear();
        if (this.year != 0 && this.year > currentYear) {
            // 需求的年还没到
            return getFirstTimeOfYear(this.year);
        }

        if (this.year != 0 && this.year < currentYear) {
            // 当前这一年已经过了
            return MAX_LONG;
        }

        // 年数ok
        // 看下月份
        int currentMonth = fromDateTime.getMonthOfYear();

        if (hasMonthLimit) {
            for (int monthConfig : this.month) {
                if (monthConfig == currentMonth) {
                    // 月份ok
                    DurationTime result = getNextTimeWithYearAndMonth(currentYear, currentMonth, fromDateTime);
                    if (result != MAX_LONG) {
                        // 这个月找到了
                        return result;
                    }
                    // 没找到
                    continue;
                }

                if (monthConfig > currentMonth) {
                    // 找这个月的第一天
                    DurationTime result = getFirstOfYearMonth(currentYear, monthConfig); // 可能失败
                    if (result != MAX_LONG) {
                        return result;
                    }
                }
            }

            // 今年没了, 看下明年行不行
            if (this.year != 0) {
                // 年数已经固定了
                return MAX_LONG;
            }

            return getFirstTimeOfYear(currentYear + 1); // 返回明年第一个符合的日子
        } else {
            // 月份ok
            DurationTime result = getNextTimeWithYearAndMonth(currentYear, currentMonth, fromDateTime);
            if (result != MAX_LONG) {
                return result;
            }
            // 这个月没有, 找下个月
            for (int tryMonth = currentMonth + 1; tryMonth <= 12; tryMonth++) {
                result = getFirstOfYearMonth(currentYear, tryMonth); // 可能失败
                if (result != MAX_LONG) {
                    return result;
                }
            }
            // 今年没了, 看下明年行不行
            if (this.year != 0) {
                // 年数已经固定了
                return MAX_LONG;
            }

            return getFirstTimeOfYear(currentYear + 1); // 返回明年第一个符合的日子
        }
    }

    /**
     * 找下这一年的这一个月里, 还有没有符合要求的. 没有的话返回0
     * @param currentYear
     * @param currentMonth
     * @param fromDateTime
     * @return
     */
    private DurationTime getNextTimeWithYearAndMonth(int currentYear, int currentMonth, DateTime fromDateTime) {
        if (hasDayLimit) {
            // 有日期要求
            if (isWeekday) {
                // 是星期几的要求
                int currentDay = fromDateTime.getDayOfWeek();
                for (int dayConfig : this.days) {
                    if (dayConfig == currentDay) {
                        // 这一天ok
                        DurationTime result = getNextTimeWithYearAndMonthAndDay(fromDateTime);
                        if (result != MAX_LONG) {
                            return result;
                        }
                        // 这一天里没有, 找下一天
                        continue;
                    }

                    if (dayConfig > currentDay) {
                        // 看下这天是不是超过了这个月了
                        DateTime newTime = fromDateTime.plusDays(dayConfig - currentDay);
                        if (newTime.getMonthOfYear() != currentMonth) {
                            // 已经不是这个月的了
                            return MAX_LONG;
                        }
                        // 加了几天后, 还是这个月的
                        // 取这一天的第一个时间

                        int hour = this.hours[0].left;
                        int endHour = this.hours[0].right;
                        long startTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0).getMillis();
                        long endTime = newTime.withTime(Utils.getHighShort(endHour), Utils.getLowShort(endHour), 0, 0).getMillis();
                        return new DurationTime(startTime, endTime);
                    }
                }
                // 到这里, 这一周都没有, 看下下一周
                int diff = days[0] + 7 - currentDay;
                DateTime newTime = fromDateTime.plusDays(diff);
                if (newTime.getMonthOfYear() != currentMonth) {
                    // 已经不是这个月的了
                    return MAX_LONG;
                }
                // 加了几天后, 还是这个月的
                // 取这一天的第一个时间

                int hour = this.hours[0].left;
                int endHour = this.hours[0].right;
                long startTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0).getMillis();
                long endTime = newTime.withTime(Utils.getHighShort(endHour), Utils.getLowShort(endHour), 0, 0).getMillis();
                return new DurationTime(startTime, endTime);
            } else {
                // 是日期要求
                int currentDay = fromDateTime.getDayOfMonth();
                for (int dayConfig : this.days) {
                    if (dayConfig == currentDay) {
                        // 这一天ok
                        DurationTime result = getNextTimeWithYearAndMonthAndDay(fromDateTime);
                        if (result != MAX_LONG) {
                            return result;
                        }
                        // 这一天里没有了
                        continue;
                    }
                    if (dayConfig > currentDay) {
                        // 直接取这一天的第一个时间
                        int hour = hours[0].left;
                        long newTime = fromDateTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0).getMillis();
                        long endTime = fromDateTime.withTime(Utils.getHighShort(hours[0].right), Utils.getLowShort(hours[0].right), 0, 0).getMillis();
                        long diff = dayConfig - currentDay;
                        return new DurationTime(newTime + DateTimeConstants.MILLIS_PER_DAY * diff, endTime + DateTimeConstants.MILLIS_PER_DAY * diff);
                    }
                }

                // 到这里都没有. 这个月没有了
                return MAX_LONG;
            }
        } else {
            // 无日期要求
            // 看下这一天里有没有符合的
            DurationTime result = getNextTimeWithYearAndMonthAndDay(fromDateTime);
            if (result != MAX_LONG) {
                return result;
            }
            // 看下加一天, 还是不是这个月了
            DateTime newTime = fromDateTime.plusDays(1);
            if (newTime.getMonthOfYear() != currentMonth) {
                return MAX_LONG; // 今天已经是这一天最后一天了
            }

            int hour = this.hours[0].left;
            int endHour = this.hours[0].right;
            long startTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0).getMillis();
            long endTime = newTime.withTime(Utils.getHighShort(endHour), Utils.getLowShort(endHour), 0, 0).getMillis();
            return new DurationTime(startTime, endTime);
        }
    }

    private DurationTime getNextTimeWithYearAndMonthAndDay(DateTime fromDateTime) {
        int hour = fromDateTime.getHourOfDay();
        int minute = fromDateTime.getMinuteOfHour();
        int currentHour = Utils.short2Int(hour, minute);

        for (IntPair pair : hours) {
            int hm = pair.left;
            if (hm >= currentHour) {
                // 今天后面还有. 算下时间

                int h = Utils.getHighShort(hm);
                int m = Utils.getLowShort(hm);
                int totalMillis = h * DateTimeConstants.MILLIS_PER_HOUR + m * DateTimeConstants.MILLIS_PER_MINUTE; // 算出在这一天中的millis

                int fromDateTimeTotalMillis = fromDateTime.getMillisOfDay(); // 算出当前时间在这一天中的millis
                long startTime = fromDateTime.getMillis() + (totalMillis - fromDateTimeTotalMillis);
                h = Utils.getHighShort(pair.right);
                m = Utils.getLowShort(pair.right);
                totalMillis = h * DateTimeConstants.MILLIS_PER_HOUR + m * DateTimeConstants.MILLIS_PER_MINUTE; // 算出在这一天中的millis

                fromDateTimeTotalMillis = fromDateTime.getMillisOfDay(); // 算出当前时间在这一天中的millis
                long endTime = fromDateTime.getMillis() + (totalMillis - fromDateTimeTotalMillis);
                return new DurationTime(startTime, endTime);
            }
        }
        return MAX_LONG;
    }

    private DurationTime getFirstTimeOfYear(int currentYear) {
        return doGetFirstTimeOfYear(currentYear, 0);
    }

    private DurationTime doGetFirstTimeOfYear(int currentYear, int level) {
        if (level >= 8) {
            return MAX_LONG; // 最多递归8层
        }

        if (hasMonthLimit) {
            for (int tryMonth : month) {
                DurationTime result = getFirstOfYearMonth(currentYear, tryMonth); // 如果没有, 则看下个月, 看完看下一年
                if (result != MAX_LONG) {
                    return result;
                }
            }

            // 到这里都没有, 今年没了
            if (this.year == 0) {
                return doGetFirstTimeOfYear(currentYear + 1, level + 1); // 递归, 可能完蛋
            } else {
                return MAX_LONG;
            }
        }
        return getFirstOfYearMonth(currentYear, 1); // 一定符合要求. 1月份肯定有31号
    }

    /**
     * 得到这一年, 这个月第一个符合要求的时间, 如果没有, 返回0. 一定要求日期, 而且这个日期这个月是没有的
     * @param currentYear
     * @param currentMonth
     * @return
     */
    private DurationTime getFirstOfYearMonth(int currentYear, int currentMonth) {
        int hour = hours[0].left;
        int end = hours[0].right;
        if (!hasDayLimit) {
            // 没有日期限制
            long startTime = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(hour), Utils.getLowShort(hour)).getMillis();
            long endTime = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(end), Utils.getLowShort(end)).getMillis();

            return new DurationTime(startTime, endTime);
        }

        assert days.length > 0;
        if (!isWeekday) {
            // 有日期限制, 但是是直接写的日期

            int day = days[0];
            if (day <= 28) {
                // 一定有
                long startTime = new DateTime(currentYear, currentMonth, days[0], Utils.getHighShort(hour), Utils.getLowShort(hour)).getMillis();
                long endTime = new DateTime(currentYear, currentMonth, days[0], Utils.getHighShort(end), Utils.getLowShort(end)).getMillis();

                return new DurationTime(startTime, endTime);
            } else {
                // 判断下这个月有没有这么大的日期
                DateTime time = new DateTime(currentYear, currentMonth, 1, 0, 0);
                int dayLimit = time.dayOfMonth().getMaximumValue();
                if (dayLimit >= day) {
                    // 这一天在这个月是有的
                    //                    return new DateTime(currentYear, currentMonth, day, Utils.getHighShort(hour), Utils.getLowShort(hour)).getMillis();
                    long startTime = new DateTime(currentYear, currentMonth, day, Utils.getHighShort(hour), Utils.getLowShort(hour)).getMillis();
                    long endTime = new DateTime(currentYear, currentMonth, day, Utils.getHighShort(end), Utils.getLowShort(end)).getMillis();

                    return new DurationTime(startTime, endTime);
                } else {
                    // 这个月没有这一天
                    return MAX_LONG;
                }
            }
        }
        // 有日期限制, 而且是星期
        // 看下这个月第一天是星期几
        DateTime time = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(hour), Utils.getLowShort(hour));
        DateTime endTime = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(end), Utils.getLowShort(end));

        int dayOfWeek = time.getDayOfWeek();
        for (int dayConfig : this.days) {
            if (dayConfig == dayOfWeek) {
                return new DurationTime(time.getMillis(), endTime.getMillis()); // 这一天是允许的
            }
            if (dayConfig > dayOfWeek) {
                // 这个星期几刚好大于当前的星期几
                long diff = dayConfig - dayOfWeek; // 要延后几天
                return new DurationTime(time.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY,
                        endTime.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY);
            }
        }
        // 结束了, 没有配置的星期比当前的大, 到下一个星期
        int diff = days[0] + 7 - dayOfWeek;
        return new DurationTime(time.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY,
                endTime.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY);
    }

    public DurationTime getRealBeforeTime(long from) {
        DurationTime beforeTime = getBeforeTime(from);
        if (beforeTime.startTime != from) {
            return beforeTime;
        }
        return getBeforeTime(from - DateTimeConstants.MILLIS_PER_MINUTE);
    }

    /**
     * 得到下一个符合要求的时间点. 没有就返回0,这个时间点必然是传入的时间点+1分钟之后
     * @param form
     * @return
     */
    public DurationTime getRealNextTime(long form) {
        DurationTime nextTime = getNextTime(form);
        if (nextTime.startTime == form) {
            return getNextTime(form + DateTimeConstants.MILLIS_PER_MINUTE);
        }
        return nextTime;
    }

    public DurationTime getBeforeTime(long from) {
        DateTime fromDateTime = new DateTime(from);

        int currentYear = fromDateTime.getYear();
        if (this.year != 0 && this.year > currentYear) {
            // 需求的年还没到
            return MIN_LONG;
        }

        if (this.year != 0 && this.year < currentYear) {
            // 当前这一年已经过了
            return getLastTimeOfYear(this.year);
        }

        // 年数ok
        // 看下月份
        int currentMonth = fromDateTime.getMonthOfYear();

        if (hasMonthLimit) {
            for (int i = month.length - 1; i >= 0; i--) {
                int monthConfig = month[i];
                if (monthConfig == currentMonth) {
                    // 月份ok
                    DurationTime result = getBeforeTimeWithYearAndMonth(currentYear, currentMonth, fromDateTime);
                    if (result != MIN_LONG) {
                        // 这个月找到了
                        return result;
                    }
                    // 没找到
                    continue;
                }

                if (monthConfig < currentMonth) {
                    // 找这个月的第一天
                    DurationTime result = getLastOfYearMonth(currentYear, monthConfig); // 可能失败
                    if (result != MIN_LONG) {
                        return result;
                    }
                }
            }

            // 今年没了, 看下明年行不行
            if (this.year != 0) {
                // 年数已经固定了
                return MIN_LONG;
            }

            return getLastTimeOfYear(currentYear - 1); // 返回明年第一个符合的日子
        } else {
            // 月份ok
            DurationTime result = getBeforeTimeWithYearAndMonth(currentYear, currentMonth, fromDateTime);
            if (result != MIN_LONG) {
                return result;
            }
            // 这个月没有, 找下个月
            for (int tryMonth = currentMonth - 1; tryMonth >= 1; tryMonth--) {
                result = getLastOfYearMonth(currentYear, tryMonth); // 可能失败
                if (result != MIN_LONG) {
                    return result;
                }
            }
            // 今年没了, 看下明年行不行
            if (this.year != 0) {
                // 年数已经固定了
                return MIN_LONG;
            }

            return getLastTimeOfYear(currentYear - 1); // 返回明年第一个符合的日子
        }
    }

    /**
     * 找下这一年的这一个月里, 还有没有符合要求的. 没有的话返回0
     * @param currentYear
     * @param currentMonth
     * @param fromDateTime
     * @return
     */
    private DurationTime getBeforeTimeWithYearAndMonth(int currentYear, int currentMonth, DateTime fromDateTime) {
        if (hasDayLimit) {
            // 有日期要求
            if (isWeekday) {
                // 是星期几的要求
                int currentDay = fromDateTime.getDayOfWeek();
                for (int i = days.length - 1; i >= 0; i--) {
                    int dayConfig = days[i];
                    if (dayConfig == currentDay) {
                        // 这一天ok
                        DurationTime result = getBeforeTimeWithYearAndMonthAndDay(fromDateTime);
                        if (result != MIN_LONG) {
                            return result;
                        }
                        // 这一天里没有, 找下一天
                        continue;
                    }

                    if (dayConfig < currentDay) {
                        // 看下这天是不是超过了这个月了
                        DateTime newTime = fromDateTime.plusDays(dayConfig - currentDay);
                        if (newTime.getMonthOfYear() != currentMonth) {
                            // 已经不是这个月的了
                            return MIN_LONG;
                        }
                        // 加了几天后, 还是这个月的
                        // 取这一天的第一个时间

                        int hour = this.hours[hours.length - 1].left;
                        int end = this.hours[hours.length - 1].right;
                        newTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0);
                        DateTime endTime = newTime.withTime(Utils.getHighShort(end), Utils.getLowShort(end), 0, 0);
                        return new DurationTime(newTime.getMillis(), endTime.getMillis());
                    }
                }
                // 到这里, 这一周都没有, 看下上一周
                int diff = -(7 - days[days.length - 1]) - currentDay;
                DateTime newTime = fromDateTime.plusDays(diff);
                if (newTime.getMonthOfYear() != currentMonth) {
                    // 已经不是这个月的了
                    return MIN_LONG;
                }
                // 加了几天后, 还是这个月的
                // 取这一天的第一个时间

                int hour = this.hours[hours.length - 1].left;
                int end = this.hours[hours.length - 1].right;
                newTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0);
                DateTime endTime = newTime.withTime(Utils.getHighShort(end), Utils.getLowShort(end), 0, 0);
                return new DurationTime(newTime.getMillis(), endTime.getMillis());
            } else {
                // 是日期要求
                int currentDay = fromDateTime.getDayOfMonth();
                for (int i = 0; i < this.days.length; i++) {
                    int dayConfig = this.days[i];
                    if (dayConfig == currentDay) {
                        // 这一天ok
                        DurationTime result = getBeforeTimeWithYearAndMonthAndDay(fromDateTime);
                        if (result != MIN_LONG) {
                            return result;
                        }
                        // 这一天里没有了
                        continue;
                    }
                    if (dayConfig < currentDay) {
                        // 直接取这一天的第一个时间
                        int hour = hours[hours.length - 1].left;
                        int end = this.hours[hours.length - 1].right;
                        long diff = dayConfig - currentDay;
                        DateTime newTime = fromDateTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0);
                        DateTime endTime = fromDateTime.withTime(Utils.getHighShort(end), Utils.getLowShort(end), 0, 0);
                        return new DurationTime(newTime.getMillis() + DateTimeConstants.MILLIS_PER_DAY * diff,
                                endTime.getMillis() + DateTimeConstants.MILLIS_PER_DAY * diff);
                    }
                }

                // 到这里都没有. 这个月没有了
                return MIN_LONG;
            }
        } else {
            // 无日期要求
            // 看下这一天里有没有符合的
            DurationTime result = getBeforeTimeWithYearAndMonthAndDay(fromDateTime);
            if (result != MIN_LONG) {
                return result;
            }
            // 看下加一天, 还是不是这个月了
            DateTime newTime = fromDateTime.plusDays(-1);
            if (newTime.getMonthOfYear() != currentMonth) {
                return MIN_LONG; // 今天已经是这一天最后一天了
            }

            int hour = hours[hours.length - 1].left;
            int end = this.hours[hours.length - 1].right;
            newTime = newTime.withTime(Utils.getHighShort(hour), Utils.getLowShort(hour), 0, 0);
            DateTime endTime = newTime.withTime(Utils.getHighShort(end), Utils.getLowShort(end), 0, 0);
            return new DurationTime(newTime.getMillis(), endTime.getMillis());
        }
    }

    private DurationTime getBeforeTimeWithYearAndMonthAndDay(DateTime fromDateTime) {
        int hour = fromDateTime.getHourOfDay();
        int minute = fromDateTime.getMinuteOfHour();
        int currentHour = Utils.short2Int(hour, minute);

        for (int i = hours.length - 1; i >= 0; i--) {
            int hm = hours[i].left;
            int end = hours[i].right;
            if (hm <= currentHour) {
                // 今天后面还有. 算下时间

                int h = Utils.getHighShort(hm);
                int m = Utils.getLowShort(hm);
                int totalMillis = h * DateTimeConstants.MILLIS_PER_HOUR + m * DateTimeConstants.MILLIS_PER_MINUTE; // 算出在这一天中的millis

                int fromDateTimeTotalMillis = fromDateTime.getMillisOfDay(); // 算出当前时间在这一天中的millis
                long startTime = fromDateTime.getMillis() + (totalMillis - fromDateTimeTotalMillis);
                h = Utils.getHighShort(end);
                m = Utils.getLowShort(end);
                totalMillis = h * DateTimeConstants.MILLIS_PER_HOUR + m * DateTimeConstants.MILLIS_PER_MINUTE; // 算出在这一天中的millis

                fromDateTimeTotalMillis = fromDateTime.getMillisOfDay(); // 算出当前时间在这一天中的millis
                long endTime = fromDateTime.getMillis() + (totalMillis - fromDateTimeTotalMillis);
                return new DurationTime(startTime, endTime);
            }
        }
        return MIN_LONG;
    }

    private DurationTime getLastTimeOfYear(int currentYear) {
        return doGetLastTimeOfYear(currentYear, 0);
    }

    private DurationTime doGetLastTimeOfYear(int currentYear, int level) {
        if (level >= 8) {
            return MIN_LONG; // 最多递归8层
        }

        if (hasMonthLimit) {
            for (int i = month.length - 1; i >= 0; i--) {
                int tryMonth = month[i];
                DurationTime result = getLastOfYearMonth(currentYear, tryMonth); // 如果没有, 则看下个月, 看完看下一年
                if (result != MIN_LONG) {
                    return result;
                }
            }

            // 到这里都没有, 今年没了
            if (this.year == 0) {
                return doGetLastTimeOfYear(currentYear - 1, level + 1); // 递归, 可能完蛋
            } else {
                return MIN_LONG;
            }
        }
        return getLastOfYearMonth(currentYear, 12); // 一定符合要求. 12月份肯定有31号
    }

    /**
     * 得到这一年, 这个月第一个符合要求的时间, 如果没有, 返回0. 一定要求日期, 而且这个日期这个月是没有的
     * @param currentYear
     * @param currentMonth
     * @return
     */
    private DurationTime getLastOfYearMonth(int currentYear, int currentMonth) {
        int hour = hours[hours.length - 1].left;
        int end = hours[hours.length - 1].right;
        if (!hasDayLimit) {
            // 没有日期限制，最后一天
            DateTime time = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(hour), Utils.getLowShort(hour)).plusMonths(1).plusDays(-1);
            DateTime endTime = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(end), Utils.getLowShort(end)).plusMonths(1).plusDays(-1);
            return new DurationTime(time.getMillis(), endTime.getMillis());
        }

        assert days.length > 0;
        if (!isWeekday) {
            // 有日期限制, 但是是直接写的日期

            for (int i = days.length - 1; i >= 0; i--) {
                int day = days[i];
                if (day <= 28) {
                    // 一定有
                    DateTime time = new DateTime(currentYear, currentMonth, day, Utils.getHighShort(hour), Utils.getLowShort(hour));
                    DateTime endTime = new DateTime(currentYear, currentMonth, day, Utils.getHighShort(end), Utils.getLowShort(end));
                    return new DurationTime(time.getMillis(), endTime.getMillis());
                } else {
                    // 判断下这个月有没有这么大的日期
                    DateTime time = new DateTime(currentYear, currentMonth, 1, 0, 0);
                    int dayLimit = time.dayOfMonth().getMaximumValue();
                    if (dayLimit >= day) {
                        // 这一天在这个月是有的
                        return new DurationTime(
                                new DateTime(currentYear, currentMonth, day, Utils.getHighShort(hour), Utils.getLowShort(hour)).getMillis(),
                                new DateTime(currentYear, currentMonth, day, Utils.getHighShort(end), Utils.getLowShort(end)).getMillis());
                    } else {
                        // 这个月没有这一天
                    }
                }
            }
            return MIN_LONG;
        }
        // 有日期限制, 而且是星期
        // 看下这个月第一天是星期几
        DateTime time = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(hour), Utils.getLowShort(hour)).plusMonths(1).plusDays(-1);
        DateTime endTime = new DateTime(currentYear, currentMonth, 1, Utils.getHighShort(end), Utils.getLowShort(end)).plusMonths(1).plusDays(-1);

        int dayOfWeek = time.getDayOfWeek();
        for (int i = this.days.length - 1; i >= 0; i--) {
            int dayConfig = this.days[i];
            if (dayConfig == dayOfWeek) {
                return new DurationTime(time.getMillis(), endTime.getMillis()); // 这一天是允许的
            }
            if (dayConfig < dayOfWeek) {
                // 这个星期几刚好大于当前的星期几
                long diff = dayConfig - dayOfWeek; // 要提前几天
                return new DurationTime(time.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY,
                        endTime.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY);
            }
        }
        // 结束了, 没有配置的星期比当前的小, 到上一个星期
        int diff = -(7 - days[days.length - 1]) - dayOfWeek;
        return new DurationTime(time.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY,
                endTime.getMillis() + diff * DateTimeConstants.MILLIS_PER_DAY);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{year, Arrays.hashCode(month), Arrays.hashCode(days), Arrays.hashCode(hours), isWeekday ? 0 : 1});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeData) {
            TimeData td = (TimeData) obj;

            if (year != td.year) {
                return false;
            }

            if (!Arrays.equals(month, td.month)) {
                return false;
            }

            if (!Arrays.equals(days, td.days)) {
                return false;
            }

            if (isWeekday != td.isWeekday) {
                return false;
            }

            if (hasDayLimit != td.hasDayLimit) {
                return false;
            }

            if (hasMonthLimit != td.hasMonthLimit) {
                return false;
            }

            if (!Arrays.equals(hours, td.hours)) {
                return false;
            }

            return true;
        }
        return false;
    }

    private static int[] toArray(Collection<Integer> input) {
        if (input.size() == 0) {
            return Empty.INT_ARRAY;
        }

        int[] result = new int[input.size()];
        int index = 0;
        for (Integer i : input) {
            result[index++] = i.intValue();
        }

        Arrays.sort(result);
        return result;
    }

    private static IntPair[] toPairArray(Collection<IntPair> input) {
        if (input.size() == 0) {
            return Empty.INT_PAIR_ARRAY;
        }

        IntPair[] result = new IntPair[input.size()];
        int index = 0;
        for (IntPair i : input) {
            result[index++] = i;
        }

        Arrays.sort(result, Comparator.comparingInt(o -> o.left));
        return result;
    }


}

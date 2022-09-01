package com.ejjiu.image.utils;

/**
 * fangshuai
 * 2018/9/7
 */
public class DurationTime {
    public final long startTime;
    public final int startSecondTime;
    public final long endTime;
    public final int endSecondTime;

    public DurationTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.startSecondTime = TimeUtils.millsToSecond(startTime);
        this.endTime = endTime;
        this.endSecondTime = TimeUtils.millsToSecond(endTime);
    }

    //    public DurationTime(DurationProto proto) {
    //        this.startTime = proto.getStartTime();
    //        this.startSecondTime = TimeUtils.millsToSecond(startTime);
    //        this.endTime = proto.getEndTime();
    //        this.endSecondTime = TimeUtils.millsToSecond(endTime);
    //    }
    //
    //    public DurationSecondProto encodeSecond() {
    //        return DurationSecondProto.newBuilder().setStartTime(startSecondTime).setEndTime(endSecondTime).build();
    //    }
    //
    //    public DurationProto encode() {
    //        return DurationProto.newBuilder().setStartTime(startTime).setEndTime(endTime).build();
    //    }

    public boolean isInTime(long ctime) {
        return startTime <= ctime && ctime < endTime;
    }

    @Override
    public int hashCode() {
        return startSecondTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DurationTime) {
            DurationTime o = (DurationTime) obj;
            return this.startTime == o.startTime && this.endTime == o.endTime;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + TimeUtils.printTime2(startTime) + "#" + TimeUtils.printTime2(endTime) + "]";
    }
}

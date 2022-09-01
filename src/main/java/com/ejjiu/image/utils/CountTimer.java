package com.ejjiu.image.utils;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/10/29 14:27
 */
public class CountTimer {

    private static final Logger logger = LoggerFactory.getLogger(CountTimer.class);
    private final String name;
    private List<DurationRecord> list = new ArrayList<>();
    private Map<String, DurationRecord> countMap = Maps.newConcurrentMap();
    @Getter
    private long total;

    public CountTimer(String name) {
        this.name = name;
        addCount(name);
    }

    public void addCount(String flag) {
        list.add(new DurationRecord(flag, System.currentTimeMillis()));
    }

    public void count(String flag, long cost) {
        DurationRecord durationRecord = countMap.computeIfAbsent(flag, s -> new DurationRecord(s, 0));
        durationRecord.time.addAndGet(cost);

    }

    public StringBuilder print() {
        
        StringBuilder sb = new StringBuilder();
        total = 0;
        if (list.size() > 0) {


            int size = list.size();
            for (int i = 1; i < size; i++) {
                DurationRecord prev = list.get(i - 1);
                DurationRecord curr = list.get(i);
                long inter = curr.time.get() - prev.time.get();
                total += inter;
                sb.append(StringUtils.rightFill(prev.flag, 30, " "));
                sb.append("->");
                sb.append(StringUtils.rightFill(curr.flag, 30, " "));
                sb.append(" cost ms:");
                sb.append(inter);
                sb.append("\n");
            }
            String count = StringUtils.leftFill("->", 32, " ") + StringUtils.leftFill("total cost ms:", 39, "=") + total;
            logger.debug("========={}===========\n{}{}", name, sb, count);
        } else if (countMap.size() > 0) {

            for (Entry<String, DurationRecord> entry : countMap.entrySet()) {
                sb.append(StringUtils.rightFill(entry.getKey(), 30, " "));
                sb.append("cost ms:" + entry.getValue().time.get());
                sb.append("\n");
                total+= entry.getValue().time.get();
            }
            String count = StringUtils.leftFill("->", 32, " ") + StringUtils.leftFill("total cost ms:", 39, "=") + total;
            logger.debug("========={}===========\n{}{}", name, sb, count);

        }
        return sb;

    }

    public void reset() {
        list.clear();
        countMap.clear();
    }

    static class DurationRecord {
        private final String flag;
        private AtomicLong time;

        public DurationRecord(String flag, long time) {
            this.flag = flag;
            this.time = new AtomicLong(time);
        }
    }
}

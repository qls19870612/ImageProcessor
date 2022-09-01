package com.ejjiu.image.db;

import com.ejjiu.image.utils.StringUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2018/11/17 20:42
 */
public class TableField {
    public static final TableField[] EMPTY = new TableField[0];
    @Getter
    private final String compoundLabel;

    public final String fieldName;
    public final String upLowerFieldName;
    public String type;
    public int size;
    public final String desc;

    @Getter
    @Setter
    private boolean selected = false;
    private static AtomicInteger counter = new AtomicInteger(0);
    public final int id;

    public TableField(String fieldName, String type, int size, String desc) {

        this.fieldName = fieldName;
        //VARCHAR(150),把括号后面的去掉
        int index = type.indexOf("(");
        if (index == -1) {
            this.type = type.toUpperCase();

        } else {
            this.type = type.substring(0, index).toUpperCase();
        }
        upLowerFieldName = StringUtils.toUpLowerString(fieldName);
        this.size = size;
        this.desc = desc;
        id = counter.incrementAndGet();
        compoundLabel = fieldName + "(" + upLowerFieldName + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableField that = (TableField) o;
        boolean b = Objects.equals(fieldName, that.fieldName) && type.equalsIgnoreCase(that.type) && Objects.equals(desc, that.desc);
        if (!b) {
            return false;
        }
        //当日期相关类型不用判断
        if (type.equalsIgnoreCase("DATETIME") || type.equalsIgnoreCase("date")) {
            return true;
        }
        return size == that.size;
    }

    @Override
    public int hashCode() {

        return Objects.hash(fieldName, type, size, desc);
    }

    @Override
    public String toString() {
        return "TableField{" + "fieldName='" + fieldName + '\'' + ", type='" + type + '\'' + ", size=" + size + ", desc='" + desc + '\'' + '}';
    }

    /**
     * 判断是否日期相关类型
     * @return boolean
     */
    public boolean isDate() {
        return type.equalsIgnoreCase("datetime") || type.equalsIgnoreCase("date");
    }
}

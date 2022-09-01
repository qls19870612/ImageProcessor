package com.ejjiu.image.utils;

/**
 * @描述
 * @创建人 liangsong
 * @创建时间 2018/7/11/011 11:41
 */
public enum FieldMergerEnum {
    NONE(0), SUFFIX(1), PARSER(2);

    private final int value1;

    private FieldMergerEnum(int value1) {
        this.value1 = value1;
    }
}

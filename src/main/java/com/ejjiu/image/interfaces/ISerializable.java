package com.ejjiu.image.interfaces;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/09/02 17:06
 */
public interface ISerializable {
    String encode();
    void decode(String value);
}

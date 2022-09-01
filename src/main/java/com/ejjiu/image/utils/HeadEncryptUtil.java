package com.ejjiu.image.utils;


import org.apache.commons.lang3.RandomUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/08/18 20:21
 */
public class HeadEncryptUtil {
    public static final int HEAD_LEN=8;
    public static byte[] encode(byte[] src, byte[] pwd) {

        byte[] bytes = src;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) ((int) bytes[i] ^ (int) pwd[i % pwd.length]);
        }

        return bytes;
    }

    private static byte[] randomKey() {
        int len = 8;
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) RandomUtils.nextInt(1,256);
        }
        return ret;
    }

    public static byte[] deEncrypt(byte[] bytes) {
        byte[] key = Arrays.copyOf(bytes,HEAD_LEN);
        bytes = Arrays.copyOfRange(bytes,HEAD_LEN,bytes.length);
        byte[] content = encode(bytes, key);
        return content;
    }
    public static String deEncryptToUtf8(byte[] bytes) {
        return new String(deEncrypt(bytes), StandardCharsets.UTF_8);
    }
    public static byte[] encrypt(String text) {
        return encrypt(text.getBytes());
    }
    public static byte[] encrypt(byte[] bytes) {

        byte[] key = randomKey();
        byte[] encode = encode(bytes, key);
        byte[] target = new byte[key.length + encode.length];

        System.arraycopy(key,0,target,0,key.length);
        System.arraycopy(encode,0,target,key.length,encode.length);

        return target;
    }
    public static String encryptToHexStr(String text) {
        byte[] encrypt = encrypt(text);
        return HexUtils.toHexString(encrypt);
    }
    public static String deEncryptHexStr(String hexText)
    {
        byte[] bytes = HexUtils.fromHexString(hexText);
        return deEncryptToUtf8(bytes);
    }

}

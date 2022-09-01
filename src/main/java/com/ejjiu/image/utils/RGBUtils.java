package com.ejjiu.image.utils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/01/18 11:13
 */
public class RGBUtils {

    public static int mergeRGB(int r, int g, int b, int a) {

        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));

    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static  int getBlue(int rgb) {
        return (rgb) & 0xFF;
    }

    public static int getAlpha(int rgb) {
        return (rgb >> 24) & 0xff;
    }

}

package com.ejjiu.image.utils;

import org.jetbrains.annotations.NotNull;

import javafx.scene.image.Image;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/11 15:48
 */
public class IconUtils {
    public static Image createStaticIcon(String name)
    {
        String url = getIconUrl(name);
        return new Image(url);
    }
    
    @NotNull
    public static String getIconUrl(String name) {
        String url = "statics/icons/" + name;
        return url;
    }
}

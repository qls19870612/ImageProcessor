package com.ejjiu.image.controllers.images.launchView.vo;

import javafx.scene.image.Image;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/10 10:14
 */
public class DeviceInfo {
    public String name;
    private String image;
    private Image imageComponent;
    public int width;
    public int height;
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
        imageComponent = new Image(image);
    }
    
    public Image getImageComponent() {
        return imageComponent;
    }
}

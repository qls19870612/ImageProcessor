package com.ejjiu.image.controllers.images.launchView.vo;

import com.ejjiu.image.interfaces.ISerializable;
import com.ejjiu.image.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/09 14:58
 */
public class ImageInfo implements ISerializable, Serializable {
    private static int idIndex = 0;
    public final int id;
    public transient int sortIndex;
    @Getter
    private transient Image image;
    
    @Getter
    private String path;
    @Getter
    @Setter
    private double scale = 1;
    /**
     * 相对缩放，相对于设备尺寸
     */
    @Getter
    @Setter
    private boolean relativeScale = false;
    @Getter
    @Setter
    private double vPercent = 0.5;
    @Getter
    @Setter
    private double hPercent = 0.5;
    
    public ImageInfo() {
        id = ++idIndex;
    }
    
    public ImageInfo(File file) throws FileNotFoundException {
        id = ++idIndex;
        this.path = file.getAbsolutePath();
        this.image = new Image(new FileInputStream(file));
    }
    
    public double getWidth() {
        return this.image.getWidth();
    }
    
    public double getHeight() {
        return this.image.getHeight();
    }
    
    @Override
    public String encode() {
        return StringUtils.joinArray(new String[]{path, scale + "", vPercent + "", hPercent + "",relativeScale?"1":"0"}, 0, ">");
    }
    
    @Override
    public void decode(String value) {
        String[] split = value.split(">");
        if (split.length > 1) {
            scale = Double.parseDouble(split[1]);
            vPercent = Double.parseDouble(split[2]);
            hPercent = Double.parseDouble(split[3]);
        }
        if (split.length > 4) {
            relativeScale = "1".equals(split[4]);
        }
        File file = new File(split[0]);
        this.path = file.getAbsolutePath();
        
        try {
            this.image = new Image(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
 
    
    @Override
    public int hashCode() {
        return id;
    }
}

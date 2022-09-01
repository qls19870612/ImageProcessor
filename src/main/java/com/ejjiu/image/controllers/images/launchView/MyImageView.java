package com.ejjiu.image.controllers.images.launchView;


import com.ejjiu.image.controllers.images.launchView.vo.ImageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/11 10:38
 */
public class MyImageView extends Pane {
    private static final Logger logger = LoggerFactory.getLogger(MyImageView.class);
    public final ImageInfo info;
    private final Image image;
    private boolean selected = true;
    
    public MyImageView(Image image, ImageInfo info) {
        super();
        this.image = image;
        
        this.getChildren().add(new ImageView(image));
        this.info = info;
        setSelected(false);
        BorderStroke borderStroke = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(1), BorderWidths.DEFAULT);
        Border value = new Border(borderStroke);
        setBorder(value);
    }
    
 
    
    public double getRealWidth() {
        return info.getWidth();
    }
    
    public double getRealHeight() {
        return info.getHeight();
    }
    
    public boolean isTransparent(Point2D point2D) {
        int x = (int) point2D.getX();
        int y = (int) point2D.getY();
        if (x >= image.getWidth() || x < 0) {
            return true;
        }
        if (y >= image.getHeight() || y < 0) {
            return true;
        }
        int argb = image.getPixelReader().getArgb(x, y);
        return (argb >>> 24) <= 0;
    }
    
    public void setSelected(boolean value) {
        if (value == this.selected) {
            return;
        }
        this.selected = value;
        if (value) {
            setStyle("-fx-border-color: green;-fx-border-style: solid;");
        } else {
            
            setStyle("");
        }
        
    }
}

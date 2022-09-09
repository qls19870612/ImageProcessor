package com.ejjiu.image.controllers.images.launchView;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ejjiu.common.controllers.Controller;
import com.ejjiu.image.controllers.images.launchView.vo.ImageInfo;
import com.ejjiu.common.file.FileOperator;
import com.ejjiu.common.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/16 14:16
 */
public class SplashCreator {
    private static final Logger logger = LoggerFactory.getLogger(SplashCreator.class);
    private static final int BASE_SIZE = 375;
    private List<String> platforms;
    private Set<String> directions;
    @org.jetbrains.annotations.NotNull
    private final LaunchViewController controller;
    private ObservableList<ImageInfo> objects;
    
    public SplashCreator(LaunchViewController controller) {
        this.controller = controller;
        
        controller.createBtn.setOnMouseClicked(this::onClickCreateBtn);
    }
    
    private void onClickCreateBtn(MouseEvent event) {
        platforms = Lists.newArrayList();
        if (controller.platAndroid.isSelected()) {
            platforms.add("Android");
        }
        
        if (controller.platIos.isSelected()) {
            platforms.add("iOS");
        }
        if (platforms.size() <= 0) {
            controller.platIos.setSelected(true);
            platforms.add("iOS");
        }
        directions = Sets.newHashSet();
        if (controller.directionH.isSelected()) {
            directions.add("landscape");
        }
        if (controller.directionV.isSelected()) {
            directions.add("portrait");
        }
        if (directions.size() <= 0) {
            if (controller.screenDirectionBtn.isSelected()) {
                directions.add("landscape");
                controller.directionH.setSelected(true);
            } else {
                directions.add("portrait");
                controller.directionV.setSelected(true);
            }
        }
        objects = controller.imageLayerList.getItems();
        File timerFolder =
                new File(controller.splashFolderSelector.getDirectoryAbsolutePath() + "/LunchView_" + TimeUtils.printTime(System.currentTimeMillis()));
        timerFolder.mkdirs();
        for (String platform : platforms) {
            String config = FileOperator.getConfig("iconConfig/" + platform + "/Splash.json");
            List<JSONObject> jsonObjects = JSON.parseArray(config, JSONObject.class);
            File folder = null;
            JSONArray validImages = new JSONArray();
            for (JSONObject item : jsonObjects) {
                int width = item.getIntValue("width");
                int height = item.getIntValue("height");
                int shortSize = Math.min(width, height);
                float baseScale = shortSize * 1.0f / BASE_SIZE;
                float rate = width * 1.0f / height;
                if (rate >= 1) {
                    baseScale *= 0.625;
                } else if (rate >= 0.75) {
                    baseScale *= 0.75;
                } else if (rate >= 0.66) {
                    baseScale *= 0.85;
                }
                
                String orientation = width > height ? "landscape" : "portrait";
                if (!directions.contains(orientation)) {
                    continue;
                }
                
                folder = new File(timerFolder.getAbsolutePath() + "/" + item.getString("folder"));
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File image = new File(folder.getAbsolutePath() + "/" + item.getString("filename"));
                Canvas canvas = new Canvas(width, height);
                GraphicsContext g = canvas.getGraphicsContext2D();
                
                for (ImageInfo object : objects) {
                    double scale = object.getScale() * baseScale;
                    double w;
                    double h;
                    if (object.isRelativeScale()) {
                        double ratioX = width / object.getWidth();
                        double ratioY = height / object.getHeight();
                        if (ratioX < ratioY) {
                            w = width * object.getScale();
                            h = object.getHeight() / object.getWidth() * w;
                        } else {
                            h = height * object.getScale();
                            w = object.getWidth() / object.getHeight() * h;
                        }
                        
                        
                    } else {
                        w = object.getWidth() * scale;
                        h = object.getHeight() * scale;
                    }
                    double dx = width * object.getHPercent() - w / 2;
                    double dy = height * object.getVPercent() - h / 2;
                    double dw = w;
                    double dh = h;
           
                    g.drawImage(object.getImage(), 0, 0, object.getWidth(), object.getHeight(), dx, dy, dw, dh);
                }
                WritableImage writableImage = new WritableImage(width, height);
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(controller.backgroundColorPicker.getValue());
                canvas.snapshot(params, writableImage);
                BufferedImage bufferedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
                SwingFXUtils.fromFXImage(writableImage, bufferedImage);
                
                try {
                    ImageIO.write(bufferedImage, "png", image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                item.remove("width");
                item.remove("height");
                item.remove("folder");
                validImages.add(item);
            }
            
            if (platform.equals("iOS") && folder != null) {
                JSONObject contents = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("version", 1);
                info.put("author", "liang song");
                contents.put("info", info);
                contents.put("images", validImages);
                FileOperator.writeFile(folder.getAbsolutePath() + "/Contents.json", JSON.toJSONString(contents, SerializerFeature.PrettyFormat));
            }
            
            
        }
        Controller.logAndPrint("生成启动图完成:" + timerFolder.getAbsolutePath());
    }
}














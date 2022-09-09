package com.ejjiu.image.controllers.images.icon;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ejjiu.common.controllers.Controller;
import com.ejjiu.common.componet.AlertBox;
import com.ejjiu.image.controllers.images.icon.IconCornerMarkerController.CornerMarkData;
import com.ejjiu.common.file.FileOperator;
import com.ejjiu.common.utils.CountTimer;
import com.ejjiu.common.utils.StringUtils;
import com.ejjiu.common.utils.TimeUtils;

import net.sf.image4j.codec.ico.ICOEncoder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;


import static java.awt.image.BufferedImage.TYPE_INT_ARGB;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/24 15:29
 */
public class IconGenerator {
    private final IconController controller;
    private final JSONObject versionObj;
    private Canvas canvas;//先画背景颜色->图片（圆角遮罩）->角标->版本号->处理最外层遮罩
    
    
    private final HBox versionBox;
    private final Button versionNumBtn;
    private final Button versionTxtBtn;
    static HashSet<String> roundAble = Sets.newHashSet("Android", "WebApp", "Quasar", "custom");
    static HashSet<String> appleFormats = Sets.newHashSet("iOS", "macOS", "watchOS");
    static List<String> specialKeys = Arrays.asList("role", "subtype");
    private boolean previewUp;
    private WritableImage versionImage;
    private Image cornerMarker;
    
    
    public IconGenerator(IconController controller) {
        versionObj = new JSONObject();
        versionObj.put("version", 1);
        versionObj.put("author", "liang song");
        
        this.controller = controller;
        canvas = new Canvas();
        versionBox = new HBox();
        versionNumBtn = new Button(controller.versionNumBtn.getText());
        versionTxtBtn = new Button(controller.versionTxtBtn.getText());
        
        versionTxtBtn.setStyle("-fx-background-color: black; -fx-border-width: 0;-fx-background-radius: 10 0 0 10;");
        versionNumBtn.setStyle("-fx-background-color: black; -fx-border-width: 0;-fx-background-radius: 0 10 10 0;");
        Insets value = new Insets(2, 10, 2, 10);
        versionNumBtn.setPadding(value);
        versionTxtBtn.setPadding(value);
        versionNumBtn.setTextFill(Color.ALICEBLUE);
        versionTxtBtn.setTextFill(Color.ALICEBLUE);
        Font font = new Font(40);
        versionTxtBtn.setFont(font);
        versionNumBtn.setFont(font);
        versionBox.getChildren().addAll(versionTxtBtn, versionNumBtn);
        versionBox.setMouseTransparent(true);
        versionBox.setLayoutY(2000);
        controller.root.getChildren().add(versionBox);
        
    }
    
    public void generate() {
        if (!controller.generateIconFolder.isExistsDirectory()) {
            AlertBox.showAlert("请先设置生成文件路径");
            return;
        }
        
        
        CountTimer countTimer = new CountTimer("createIcon");
        generate(-1);
        countTimer.addCount("complete");
        countTimer.print();
        
        
    }
    
    private static final Logger logger = LoggerFactory.getLogger(IconGenerator.class);
    
    public void setScale(double scale) {
        
        canvas.setScaleX(scale);
        canvas.setScaleY(scale);
        
    }
    
    public void generate(int forceSize) {
        
        
        if (forceSize == 0) {
            if (canvas.getParent() != null) {
                controller.root.getChildren().remove(canvas);
            }
            return;
        }
        
        resetCanvas();
        
        
        versionImage = createVersionImage();
        cornerMarker = createCornerMarker();
        List<String> selectPlatforms = controller.iconPlatformsController.selectPlatforms();
        
        if (forceSize > 0) {
            if (selectPlatforms.size() > 0) {
                //按第一个平台展示预览
                drawTargetSizeImage(selectPlatforms.get(0), forceSize);
            } else {
                drawTargetSizeImage("custom", forceSize);
            }
            if (canvas.getParent() == null) {
                addCanvas();
            }
            
            return;
        }
        List<Integer> customSize = controller.iconSizesController.getSelectSize();
        if (selectPlatforms.size() <= 0 && customSize.size() <= 0) {
            AlertBox.showAlert("未选择任何平台或自定义尺寸！");
            return;
        }
        Controller.logAndPrint("生成:图标中....");
        String root = controller.generateIconFolder.getDirectoryAbsolutePath() + "/icon_" +
                FileOperator.getFileNameNoEx(controller.getSelectImageFile().getName()) + "_" + TimeUtils.printTime(System.currentTimeMillis());
        boolean isIcon = !controller.imageTypeImage.isSelected();//是图标，不是图片
        double srcImageWidth = controller.imageView.getImage().getWidth();
        for (String platform : selectPlatforms) {
            
            String platformFolder = root + "/" + platform + "/";
            String folder = null;
            JSONObject appleJson = null;
            List<JSONObject> appleImageObj = null;
            String appleJsonFolder = null;
            boolean appleFormat = isAppleFormat(platform);
            boolean ico = isIco(platform);
            List<BufferedImage> icoImageList = null;
            if (ico) {
                icoImageList = Lists.newArrayList();
            }
            if (appleFormat || isCordova(platform)) {
                
                appleJson = new JSONObject();
                appleImageObj = Lists.newArrayList();
                appleJson.put("images", appleImageObj);
                versionObj.put("date", TimeUtils.printTime2(System.currentTimeMillis()));
                appleJson.put("info", versionObj);
                
            }
            StringBuilder webappLinkSB = null;
            if (isWebApp(platform)) {
                webappLinkSB = new StringBuilder();
            }
            
            List<JSONObject> sizes = getSizes(platform, isIcon);
            boolean android = isAndroid(platform);
            double length = 0;
            for (JSONObject size : sizes) {
                
          
                if (!isIcon) {
                    
                    length = srcImageWidth;
                    if (android) {
                        length = length / 16 * 4;
                    } else if (appleFormat) {
                        length = length / 3;
                    }
                } else {
                    
                    length = size.getDoubleValue("size");
                }
                double scale = 1.0;
                if (size.containsKey("scale")) {
                    scale = size.getDoubleValue("scale");
                }
                length = length * scale;
                if (size.containsKey("folder")) {
                    if (android) {
                        ToggleButton selectedToggle = (ToggleButton) controller.androidFolderGroup.getSelectedToggle();
                        String customAndroidFolderName = selectedToggle.getText();
                        size.put("folder", size.getString("folder").replace("drawable", customAndroidFolderName));
                    }
                    folder = platformFolder + size.getString("folder") + "/";
                } else {
                    if (!isIcon) {
                        if (android && !size.containsKey("folder")) {
                            continue;
                        }
                        if (appleFormat) {
                            folder = platformFolder + "/" + FileOperator.getFileNameNoEx(controller.getSelectImageFile().getName()) + ".imageset/";
                        } else {
                            folder = platformFolder;
                        }
                    } else {
                        
                        folder = platformFolder;
                    }
                }
                
                drawTargetSizeImage(platform, (int) length);
                
                String fileName = getTargetFileName(platform, size, (int) scale, isIcon);
                if (webappLinkSB != null) {
                    if (webappLinkSB.length() > 0) {
                        webappLinkSB.append(FileOperator.NEXT_LINE);
                    }
                    webappLinkSB.append("<link rel=\"apple-touch-icon\"");
                    if (length != 60) {
                        webappLinkSB.append(" sizes=\"").append((int) length).append("x").append((int) length).append("\"");
                    }
                    webappLinkSB.append(" href=\"").append(fileName).append(".png\"/>");
                }
                
                
                if (size.containsKey("idiom") && appleImageObj != null) {
                    JSONObject appleObj = new JSONObject();
                    String size1 = size.getString("size");
                    appleObj.put("idiom", size.getString("idiom"));
                    appleObj.put("filename", fileName + ".png");
                    appleObj.put("scale", (int) scale + "x");
                    if (isIcon) {
                        appleObj.put("size", size1 + "x" + size1);
                        for (String specialKey : specialKeys) {
                            if (appleJson.containsKey(specialKey)) {
                                appleObj.put(specialKey, size.get(specialKey));
                            }
                        }
                    }
                    appleImageObj.add(appleObj);
                    appleJsonFolder = folder;
                }
                
                File imageFile = new File(folder + fileName + ".png");
                BufferedImage bufferedImage = writeImageFromCanvas((int) length, imageFile);
                if (icoImageList != null) {
                    icoImageList.add(bufferedImage);
                }
                
            }
            if (icoImageList != null && icoImageList.size() > 0) {
                try {
                    ICOEncoder.write(icoImageList,new File(folder + (int)length + "x" + (int)length + ".ico"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (webappLinkSB != null) {
                FileOperator.writeFile(platformFolder + "readme.txt", webappLinkSB.toString());
            }
            if (appleJson != null) {
                FileOperator.writeFile(appleJsonFolder + "Contents.json", appleJson.toString(SerializerFeature.PrettyFormat));
            }
        }
        createCustomIcon(root, customSize);
        Controller.logAndPrint("生成图标:" + root);
        
    }
    
    private boolean isIco(String platform) {
        return platform.equals("ico");
    }
    
    public boolean isAppleFormat(String platform) {
        return appleFormats.contains(platform);
    }
    
    public void createCustomIcon(String root, List<Integer> customSize) {
        
        if (customSize.size() > 0) {
            String platform = "custom";
            String folder = root + "/" + platform + "/";
            String prefixTFText = controller.iconSizesController.customImagePrefixTF.getText().trim();
            for (Integer integer : customSize) {
                int length = integer;
                drawTargetSizeImage(platform, length);
                
                String fileName = prefixTFText + length + "x" + length;
                File imageFile = new File(folder + fileName + ".png");
                writeImageFromCanvas(length, imageFile);
            }
        }
    }
    
    private boolean isCordova(String platform) {
        return platform.equals("Cordova");
    }
    
    private WritableImage createVersionImage() {
        if (controller.addBadgeCb.isSelected()) {
            versionNumBtn.setText(controller.versionNumBtn.getText());
            versionTxtBtn.setText(controller.versionTxtBtn.getText());
            IconController.replaceComponentBackgroundColor(versionNumBtn, controller.versionBgCP.getValue());
            //添加版本号
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            return versionBox.snapshot(params, null);
        }
        return null;
    }
    
    private void resetCanvas() {
        canvas.setScaleY(1);
        canvas.setScaleX(1);
        canvas.getGraphicsContext2D().clearRect(0, 0, 10000, 10000);
    }
    
    public BufferedImage writeImageFromCanvas(int length, File imageFile) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage writableImage = canvas.snapshot(params, null);
        BufferedImage bufferedImage = new BufferedImage(length, length, TYPE_INT_ARGB);
        bufferedImage = SwingFXUtils.fromFXImage(writableImage, bufferedImage);
        
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
    
    @Nullable
    public String getTargetFileName(String platform, JSONObject size, int scale, boolean isIcon) {
        String fileName;
        if (isAndroid(platform) && size.containsKey("folder")) {
            String androidFileName = controller.androidNameTF.getText();
            if (StringUtils.isNotEmpty(androidFileName)) {
                androidFileName = "ic_launcher";
            }
            fileName = androidFileName;
        } else if (size.containsKey("name")) {
            fileName = size.getString("name");
        } else if (isIcon) {
            fileName = "icon-" + size.getString("size");
            if (scale != 1) {
                fileName += "@" + scale + "x";
            }
        } else {
            String name = controller.getSelectImageFile().getName();
            fileName = FileOperator.getFileNameNoEx(name) + "x" + scale;
        }
        return fileName;
    }
    
    public void drawTargetSizeImage(String platform, int length) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, 10000, 10000);
        
        canvas.setWidth(length);
        canvas.setHeight(length);
        
        
        long imageSize = Math.round(length * (1 - controller.iconMarginsController.marginPercent.getPercent() * 2));
        double innerArc = imageSize * controller.innerCornerPercent.getPercent() * 2;
        
        graphics.setFill(controller.backgroundColorPicker.getValue());
        graphics.fillRect(0, 0, length, length);
        
        
        graphics.setFill(new ImagePattern(controller.imageView.getImage()));
        double pos = (length - imageSize) * 0.5;
        graphics.fillRoundRect(pos, pos, imageSize, imageSize, innerArc, innerArc);
        
        if (cornerMarker != null) {
            //添加右下角标
            graphics.drawImage(cornerMarker, 0, 0, length, length);
            
        }
        
        if (versionImage != null) {
            //添加版本号
            double versionWidth = Math.min(versionImage.getWidth(), length - 10);
            double versionHeight = Math.round(versionWidth * versionImage.getHeight() / versionImage.getWidth());
            
            double maxHeightPercent = 0.15;
            if (versionHeight / length > maxHeightPercent) {
                versionHeight = length * maxHeightPercent;
                versionWidth = versionHeight * (versionImage.getWidth() / versionImage.getHeight());
            }
            long versionX = Math.round((length - versionWidth) * 0.5);
            graphics.drawImage(versionImage, versionX, 0, versionWidth, versionHeight);
            
        }
        
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        
        WritableImage writableImage = new WritableImage(length, length);
        canvas.snapshot(params, writableImage);
        graphics.clearRect(0, 0, 10000, 10000);
        if (enableBackgroundTransparent(platform)) {
            graphics.setFill(Color.TRANSPARENT);
        } else {
            graphics.setFill(Color.WHITE);
        }
        graphics.fillRect(0, 0, length, length);
        //处理外部圆角
        graphics.setFill(new ImagePattern(writableImage));
        double outArc = length * controller.outConnerPercent.getPercent() * 2;
        
        graphics.fillRoundRect(0, 0, length, length, outArc, outArc);
    }
    
    public boolean enableBackgroundTransparent(String platform) {
        return roundAble.contains(platform) || (isIOS(platform) && controller.imageTypeImage.isSelected());
    }
    
    private boolean isIOS(String platform) {
        return platform.equals("iOS");
    }
    
    /**
     * 创建角标图片
     * @return
     */
    
    public Image createCornerMarker() {
        CornerMarkData selectedItem = controller.iconCornerMarkerController.connerMarkerTypeCb.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(selectedItem.data)) {
            return null;
        }
        String type = controller.iconCornerMarkerController.connerMarkerTypeCb.getSelectionModel().getSelectedItem().data;
        String color = controller.iconCornerMarkerController.connerMarkerColorCb.getSelectionModel().getSelectedItem().data;
        
        String pathname = "/iconConfig/cornerMarker/" + color + "_" + type + ".png";
        InputStream resourceAsStream = getClass().getResourceAsStream(pathname);
        Image value = new Image(resourceAsStream);
        return value;
    }
    
    
    private boolean isAndroid(String platform) {
        return platform.equals("Android");
    }
    
    public boolean isWebApp(String platform) {
        return platform.equals("WebApp");
    }
    
    private List<JSONObject> getSizes(String platform, boolean isIcon) {
        List<JSONObject> jsonObjects;
        if (!isIcon && isAppleFormat(platform)) {
            jsonObjects = Lists.newArrayList();
            for (int i = 1; i < 4; i++) {
                JSONObject sizeObj = new JSONObject();
                sizeObj.put("idiom", "universal");
                sizeObj.put("scale", i);
                
                jsonObjects.add(sizeObj);
            }
            return jsonObjects;
        }
        String url = "iconConfig/" + platform + "/Icon.json";
        
        String config = FileOperator.getConfig(url);
        
        jsonObjects = JSONObject.parseArray(config, JSONObject.class);
        if (platform.equals("iOS")) {
            List<JSONObject> retList = Lists.newArrayList();
            String level = "7+";
            if (controller.iosOldSizeCb.isSelected()) {
                level = "5+";
            }
            for (JSONObject jsonObject : jsonObjects) {
                if (!jsonObject.containsKey("level") || level.equals(jsonObject.getString("level"))) {
                    retList.add(jsonObject);
                }
            }
            return retList;
        }
        return jsonObjects;
    }
    
    public void setImagePreviewUp(boolean value) {
        previewUp = value;
        if (canvas.getParent() != null) {
            controller.root.getChildren().remove(canvas);
            addCanvas();
        }
    }
    
    public void addCanvas() {
        if (previewUp) {
            controller.root.getChildren().add(canvas);
        } else {
            controller.root.getChildren().add(0, canvas);
        }
    }
}

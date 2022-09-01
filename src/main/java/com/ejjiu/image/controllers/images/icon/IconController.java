package com.ejjiu.image.controllers.images.icon;

import com.ejjiu.image.componet.AlertBox;
import com.ejjiu.image.componet.fxml.CheckBoxComponent;
import com.ejjiu.image.componet.fxml.ColorPickerComponent;
import com.ejjiu.image.componet.fxml.FileSelector;
import com.ejjiu.image.componet.fxml.PercentComponent;
import com.ejjiu.image.componet.fxml.TextFieldComponent;
import com.ejjiu.image.componet.fxml.ToggleGroupComponent;
import com.ejjiu.image.controllers.AbstractController;
import com.ejjiu.image.controllers.images.icon.IconCornerMarkerController.CornerMarkData;
import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.file.FileOperator;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.TransformChangedEvent;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/18 9:34
 */
public class IconController extends AbstractController {
    
    private static final Logger logger = LoggerFactory.getLogger(IconController.class);
    static final double PREVIEW_MAX_SIZE = 240;
    public ImageView imageView;
    public Pane imageBg;
    public TextFieldComponent androidNameTF;
    public AnchorPane imageContainer;
    public PercentComponent innerCornerPercent;
    public PercentComponent outConnerPercent;
    public ColorPickerComponent backgroundColorPicker;
    public Button versionTxtBtn;
    public Button versionNumBtn;
    public HBox versionAlignBox;
    public ImageView imageConnerMarker;
    public ToggleGroupComponent imageTypeGroup;
    public ToggleGroupComponent androidFolderGroup;
    
    @FXML
    public IconCornerMarkerController iconCornerMarkerController;
    public CheckBoxComponent addBadgeCb;
    public ColorPickerComponent versionBgCP;
    public HBox iconPlatforms;
    public AnchorPane root;
    public HBox versionBox;
    public FileSelector generateIconFolder;
    public PercentComponent iconSizeSlider;
    public PercentComponent iconScaleSlider;
    public CheckBoxComponent previewUpCb;
    public RadioButton imageTypeIcon;
    public Label imageTypeDesLabel;
    public RadioButton imageTypeImage;
    public Label dragTipLabel;
    
    
    @Autowired
    ConfigRepository configRepository;
    public CheckBox iosOldSizeCb;
    
    @FXML
    public IconMarginsController iconMarginsController;
    @FXML
    public IconPlatformsController iconPlatformsController;
    @FXML
    public IconSizesController iconSizesController;
    private Image selectImage;
    
    public File getSelectImageFile() {
        return selectImageFile;
    }
    
    private File selectImageFile;
    private IconGenerator iconGenerator;
    
    @Override
    public void setup() {
        super.setup();
        iconSizesController.init(configRepository);
        iconCornerMarkerController.init(this::refreshCorner);
        innerCornerPercent.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, event -> refreshImageMask());
        outConnerPercent.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, event -> refreshImageBgMask());
        backgroundColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            replaceComponentBackgroundColor(imageBg, newValue);
        });
        
        replaceComponentBackgroundColor(imageBg, backgroundColorPicker.valueProperty().get());
        iconMarginsController.marginPercent.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, event -> {
            layoutImage();
            setupIcon();
        });
        versionBgCP.valueProperty().addListener((observable, oldValue, newValue) -> {
            
            replaceComponentBackgroundColor(versionNumBtn, newValue);
            
        });
        replaceComponentBackgroundColor(versionNumBtn, versionBgCP.valueProperty().get());
        layoutImageBg();
        layoutImage();
        setupIcon();
        refreshCorner();
        iconGenerator = new IconGenerator(this);
 
        iconSizeSlider.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, event ->  iconGenerator.generate((int) iconSizeSlider.getPercent()));
        iconScaleSlider.addEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, event ->  iconGenerator.setScale( iconScaleSlider.getPercent()));
        previewUpCb.selectedProperty().addListener((observable, oldValue, newValue) -> iconGenerator.setImagePreviewUp(newValue));
        iconGenerator.setImagePreviewUp(previewUpCb.isSelected());
        imageTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == imageTypeIcon) {
                imageTypeDesLabel.setText("iOS 即 appiconset；Android 即正常的图标（支持透明背景）；均为正方形");
            }
            else
            {
                imageTypeDesLabel.setText("iOS 将生成 imageset（原图 / @2x / @3x，支持透明背景）；Android 将生成宽度按照&#xD; 3:4:6:8:12:16 的比例排列的 l/m/h/xh/xxh/xxxh 序列。上传的图片将作为最大尺寸，其他尺寸等比缩小。如需其他尺寸，请使用下面的自定义大小");
            }
        });
    }
    
    static void replaceComponentBackgroundColor(Node node, Color color) {
        String webColor = StringUtils.formatHexString(color);
        String value = node.getStyle().replaceAll("-fx-background-color:[^;]+", "-fx-background-color:" + webColor + ";");
        node.setStyle(value);
    }
    
    private void refreshCorner() {
        CornerMarkData selectedItem = iconCornerMarkerController.connerMarkerTypeCb.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(selectedItem.data)) {
            imageConnerMarker.setVisible(false);
            return;
        }
        imageConnerMarker.setVisible(true);
        String type = iconCornerMarkerController.connerMarkerTypeCb.getSelectionModel().getSelectedItem().data;
        String color = iconCornerMarkerController.connerMarkerColorCb.getSelectionModel().getSelectedItem().data;
        
        String pathname = "/iconConfig/cornerMarker/" + color + "_" + type + ".png";
        InputStream resourceAsStream = getClass().getResourceAsStream(pathname);
        Image value = new Image(resourceAsStream);
        imageConnerMarker.setImage(value);
        
        
    }
    
    public void layoutImageBg() {
        imageBg.setPrefSize(PREVIEW_MAX_SIZE, PREVIEW_MAX_SIZE);
        
        imageBg.setLayoutX((imageContainer.getMaxWidth() - imageBg.getPrefWidth()) * 0.5);
        imageBg.setLayoutY((imageContainer.getMaxHeight() - imageBg.getPrefHeight()) * 0.5);
    }
    
    public void layoutImage() {
        double fitSize = PREVIEW_MAX_SIZE - PREVIEW_MAX_SIZE * iconMarginsController.marginPercent.getPercent() * 2;
        imageView.setFitHeight(fitSize);
        imageView.setFitWidth(fitSize);
        imageView.setLayoutX((imageBg.getPrefWidth() - imageView.getFitWidth()) * 0.5);
        imageView.setLayoutY((imageBg.getPrefHeight() - imageView.getFitHeight()) * 0.5);
        
    }
    
    private void setupIcon() {
        imageView.setMouseTransparent(true);
        String iconFilePath = configRepository.getConfig(ConfigType.ICON_FILE_PATH);
        File file = new File(iconFilePath);
        if (file.exists()) {
            try {
                selectImageFile = file;
                selectImage = new Image(new FileInputStream(file));
                selectImage = fixSelectImage(selectImage);
               
                renderIcon();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        refreshImageMask();
        refreshImageBgMask();
        
    }
    
    private Image fixSelectImage(Image selectImage) {
    
        if (selectImage.getWidth() == selectImage.getHeight()){
            return selectImage;
        }
        int minSize = (int) Math.max(selectImage.getWidth(), selectImage.getHeight());
//        int minSize = (int) Math.min(selectImage.getWidth(), selectImage.getHeight());
        return convertImageToSize(selectImage,minSize);
        
    }
    
    private Image convertImageToSize(Image selectImage, int sideLength) {
        Canvas canvas = new Canvas();
        canvas.setWidth(sideLength);
        canvas.setHeight(sideLength);
        int x = (int) ((selectImage.getWidth() - sideLength)/2);
        int y = (int) ((selectImage.getHeight() - sideLength)/2);
        canvas.getGraphicsContext2D().drawImage(selectImage,x,y,sideLength,sideLength,0,0,sideLength,sideLength);
 
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params,null);
    }
    
    private void refreshImageBgMask() {
        double percent = outConnerPercent.getPercent();
        Rectangle rectangle = new Rectangle(0, 0, PREVIEW_MAX_SIZE, PREVIEW_MAX_SIZE);
        
        double value = PREVIEW_MAX_SIZE * percent * 2;
        rectangle.setArcWidth(value);
        rectangle.setArcHeight(value);
        
        imageBg.setClip(rectangle);
    }
    
    private void refreshImageMask() {
        double percent = innerCornerPercent.getPercent();
        Rectangle rectangle = new Rectangle(0, 0, imageView.getFitWidth(), imageView.getFitHeight());
        
        double value = imageView.getFitWidth() * percent * 2;
        rectangle.setArcWidth(value);
        rectangle.setArcHeight(value);
        
        imageView.setClip(rectangle);
    }
    
    public void mainViewDragOver(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        
        for (File file : files) {
            if (FileOperator.isImage(file)) {
                
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
                break;
            }
        }
    }
    
    public void mainViewDragDropped(DragEvent dragEvent) throws FileNotFoundException {
        List<File> files = dragEvent.getDragboard().getFiles();
        for (File file : files) {
            if (FileOperator.isImage(file)) {
                selectImageFile = file;
                selectImage = new Image(new FileInputStream(file));
                selectImage = fixSelectImage(selectImage);
                configRepository.setConfig(ConfigType.ICON_FILE_PATH, file.getAbsolutePath());
                renderIcon();
                return;
            }
        }
        
    }
    
    private void renderIcon() {
        dragTipLabel.setVisible(selectImage == null);
        imageView.setImage(selectImage);
    }
    
    public void onGenerateBtnClick(MouseEvent event) {
        iconGenerator.generate();
    }
    
    public void userFileNameLinkClick(MouseEvent event) {
        if (selectImageFile == null) {
            AlertBox.showAlert("请选择一张图片");
            return;
        }
        androidNameTF.setText(FileOperator.getFileNameNoEx(selectImageFile.getName()));
    }
    
    public void useDefaultFileNameLinkClick(MouseEvent event) {
        androidNameTF.setText("ic_launcher");
    }
    
    public void imageContainerClick(MouseEvent event) {
        if (selectImageFile != null) {
            FileOperator.openFileAndSelect(selectImageFile.getAbsolutePath());
        }
        
    }
}

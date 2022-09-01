package com.ejjiu.image.controllers.images.launchView;

import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSONArray;
import com.ejjiu.image.componet.fxml.FileSelector;
import com.ejjiu.image.componet.fxml.PercentComponent;
import com.ejjiu.image.componet.fxml.SerializableListView;
import com.ejjiu.image.controllers.AbstractController;
import com.ejjiu.image.controllers.images.launchView.vo.DeviceInfo;
import com.ejjiu.image.controllers.images.launchView.vo.ImageInfo;
import com.ejjiu.image.events.ItemEvent;
import com.ejjiu.image.file.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.TransformChangedEvent;
import javafx.stage.FileChooser;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/04 14:35
 */
public class LaunchViewController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(LaunchViewController.class);
    public ListView<DeviceInfo> deviceList;
    public SerializableListView<ImageInfo> imageLayerList;
    public ToggleButton platIos;
    public ToggleButton platAndroid;
    public ColorPicker backgroundColorPicker;
    public Pane mainView;
    public String devices;
    public Label screenSizeLabel;
    public ToggleButton screenDirectionBtn;
    public Pane stage;
    public BorderPane borderPane;
    public ToggleButton autoScaleBtn;
    public Slider scaleSlider;
    public TextField scaleTextField;
    public PercentComponent vPercentComponent;
    public PercentComponent hPercentComponent;
    public PercentComponent scaleComponent;
    public Button createBtn;
    public ToggleButton directionH;
    public ToggleButton directionV;
    public FileSelector splashFolderSelector;
    public VBox propertyBox;
    public CheckBox relativeScaleCb;
    private DeviceInfo selectDeviceInfo;
    private static final double margin = 50;
    private double _manualScale = 0;
    private boolean draggingScale;
    private ImageInfo selectImageInfo;
    
    
    private MyImageView _selectImage;
    private Point2D _selectImageMousePressPos;
    private SplashCreator splashCreator;
    private Point2D _selectMainViewPressPos;
    
    @Override
    public void setup() {
        super.setup();
        
        imageLayerList.setCellFactory(param -> new ImageRender<>());
        deviceList.setCellFactory(param -> new DeviceRender<>());
        
        List<DeviceInfo> deviceInfos = JSONArray.parseArray(devices, DeviceInfo.class);
        deviceList.getItems().addAll(deviceInfos);
        this.selectDeviceInfo = deviceList.getItems().get(0);
        imageLayerList.addEventHandler(ItemEvent.ITEM_SELECT, event -> {
            selectImage((ImageInfo) event.info);
        });
        imageLayerList.addEventHandler(ItemEvent.ITEM_SORT, event -> {
            sortLayer();
        });
        imageLayerList.addEventHandler(ItemEvent.ITEM_DELETE, event -> {
            
            removeImage((ImageInfo) event.info);
            imageLayerList.getItems().remove(event.info);
            imageLayerList.save();
        });
        
        this.hPercentComponent.addEventFilter(TransformChangedEvent.TRANSFORM_CHANGED, event -> {
            if (this.selectImageInfo == null) {
                return;
            }
            this.selectImageInfo.setHPercent(hPercentComponent.getPercent());
            validSelectImage();
        });
        this.vPercentComponent.addEventFilter(TransformChangedEvent.TRANSFORM_CHANGED, event -> {
            if (this.selectImageInfo == null) {
                return;
            }
            this.selectImageInfo.setVPercent(vPercentComponent.getPercent());
            validSelectImage();
        });
        this.scaleComponent.addEventFilter(TransformChangedEvent.TRANSFORM_CHANGED, event -> {
            if (this.selectImageInfo == null) {
                return;
            }
            double scale = this.scaleComponent.getPercent();
            this.selectImageInfo.setScale(scale);
            validSelectImage();
        });
        
        deviceList.addEventHandler(ItemEvent.ITEM_SELECT, event -> {
            selectDeviceInfo = (DeviceInfo) event.info;
            screenSizeLabel.setText("屏幕尺寸:" + selectDeviceInfo.width + " x " + selectDeviceInfo.height);
            refreshScreenSizeAndImage();
        });
        scaleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            _manualScale = scaleSlider.getValue();
            draggingScale = true;
            refreshScreenSize();
            draggingScale = false;
        });
        backgroundColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            String s = newValue.toString();
            mainView.setStyle("-fx-border-color: black; -fx-border-radius: 1;-fx-background-color: #" + s.substring(2));
        });
        ChangeListener<Number> stageResize = (observable, oldValue, newValue) -> {
            refreshScreenSize();
        };
        stage.widthProperty().addListener(stageResize);
        stage.heightProperty().addListener(stageResize);
        
        Platform.runLater(this::refreshStage);
        splashCreator = new SplashCreator(this);
        scaleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            
            if (!newValue.matches("[\\d.]*")) {
                newValue = newValue.replaceAll("[^\\d.]", "");
                scaleTextField.setText(newValue);
            }
        });
        scaleTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                scaleSlider.adjustValue(Double.parseDouble(scaleTextField.getText()));
                autoScaleBtn.setSelected(false);
            }
        });
        relativeScaleCb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (_selectImage != null) {
                _selectImage.info.setRelativeScale(newValue);
                validImageView(_selectImage);
            }
        });
        
    }
    
    
    private void removeImage(ImageInfo info) {
        for (Node child : mainView.getChildren()) {
            if (child instanceof MyImageView) {
                if (((MyImageView) child).info == info) {
                    mainView.getChildren().remove(child);
                    if (info == _selectImage.info) {
                        selectImage(null);
                    }
                    break;
                }
            }
        }
    }
    
    
    @Override
    public void onAppClose() {
        super.onAppClose();
        imageLayerList.save();
    }
    
    private void refreshStage() {
        
        this.refreshScreenSize();
        for (ImageInfo item : imageLayerList.getItems()) {
            addImageViewToStage(item);
        }
    }
    
    private void sortLayer() {
        List<MyImageView> list = Lists.newArrayList();
        int index = 0;
        for (ImageInfo item : imageLayerList.getItems()) {
            item.sortIndex = index++;
        }
        for (Node child : mainView.getChildren()) {
            if (!(child instanceof MyImageView)) {
                return;
            }
            list.add((MyImageView) child);
        }
        list.sort(Comparator.comparingInt(o -> o.info.sortIndex));
        mainView.getChildren().clear();
        for (MyImageView myImageView : list) {
            mainView.getChildren().add(myImageView);
        }
        
    }
    
    private void validSelectImage() {
        if (this.selectImageInfo == null) {
            return;
        }
        ObservableList<Node> children = mainView.getChildren();
        for (Node child : children) {
            if (child instanceof MyImageView) {
                if (((MyImageView) child).info == selectImageInfo) {
                    validImageView((MyImageView) child);
                }
            }
        }
    }
    
    private void selectImage(ImageInfo info) {
        if (info == this.selectImageInfo) {
            return;
        }
        this.selectImageInfo = info;
        
        if (info != null) {
            vPercentComponent.setPercent(info.getVPercent());
            hPercentComponent.setPercent(info.getHPercent());
            double scale = info.getScale();
            scaleComponent.setPercent(scale);
            propertyBox.setVisible(true);
        
        } else {
            propertyBox.setVisible(false);
            vPercentComponent.reset();
            hPercentComponent.reset();
            scaleComponent.reset();
        }
        if (_selectImage != null) {
            _selectImage.setSelected(false);
        }
        
        MyImageView image = findImage(info);
        _selectImage = image;
        
        if (_selectImage != null) {
            _selectImage.setSelected(true);
        }
        relativeScaleCb.setSelected(info != null && info.isRelativeScale());
    }
    
    private MyImageView findImage(ImageInfo info) {
        for (Node child : mainView.getChildren()) {
            if (child instanceof MyImageView) {
                if (((MyImageView) child).info == info) {
                    return (MyImageView) child;
                }
            }
        }
        return null;
    }
    
    
    private int getScreenWidth() {
        if (screenDirectionBtn.isSelected()) {
            return selectDeviceInfo.height;
        }
        return selectDeviceInfo.width;
    }
    
    private int getScreenHeight() {
        if (screenDirectionBtn.isSelected()) {
            return selectDeviceInfo.width;
        }
        return selectDeviceInfo.height;
    }
    
    private void refreshScreenSize() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        
        
        screenSizeLabel.setText("屏幕尺寸:" + screenWidth + " x " + screenHeight);
        
        double scale = getScale(screenWidth, screenHeight);
        double width = screenWidth;
        double height = screenHeight;
        mainView.setMaxSize(width, height);
        mainView.setMinSize(width, height);
        mainView.setScaleX(scale);
        mainView.setScaleY(scale);
        if (autoScaleBtn.isSelected()) {
            mainView.setLayoutX((stage.getWidth() - width) / 2);
            mainView.setLayoutY((stage.getHeight() - height) / 2);
        }
        scaleTextField.setText("" + Math.round(_manualScale * 100.0) / 100.0);
        
        Rectangle value = new Rectangle(stage.getWidth(), stage.getHeight());
        
        stage.setClip(value);
    }
    
    private double getScale(int screenWidth, int screenHeight) {
        if (draggingScale) {
            return _manualScale;
        }
        if (autoScaleBtn.isSelected()) {
            
            double mainViewScaleX = (stage.getWidth() - margin * 2) / screenWidth;
            double mainViewScaleY = (stage.getHeight() - margin * 2) / screenHeight;
            
            double scale = Math.min(mainViewScaleX, mainViewScaleY);
            _manualScale = scale;
            scaleSlider.adjustValue(scale);
            return scale;
        }
        return _manualScale;
    }
    
    public void onVHToggleBtnClick(MouseEvent event) {
        refreshScreenSizeAndImage();
    }
    
    private void refreshScreenSizeAndImage() {
        refreshScreenSize();
        for (Node child : mainView.getChildren()) {
            if (!(child instanceof MyImageView)) {
                continue;
            }
            MyImageView myImageView = (MyImageView) child;
            validImageView(myImageView);
        }
    }
    
    
    public void addImageBtnClick(MouseEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(deviceList.getScene().getWindow());
        if (file == null) {
            return;
        }
        ImageInfo imageExt = null;
        try {
            imageExt = new ImageInfo(file);
            imageLayerList.getItems().add(imageExt);
            imageLayerList.save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
    }
    
    
    public void imageLayerListDragOver(DragEvent dragEvent) {
        if (dragEvent.isConsumed()) {
            return;
        }
        List<File> files = dragEvent.getDragboard().getFiles();
        for (File file : files) {
            if (FileOperator.isImage(file)) {
                dragEvent.acceptTransferModes(TransferMode.ANY);
                dragEvent.consume();
                
                return;
            }
        }
    }
    
    public void imageLayerListDragDropped(DragEvent dragEvent) throws FileNotFoundException {
        List<File> files = dragEvent.getDragboard().getFiles();
        boolean hasAdd = false;
        for (File file : files) {
            if (FileOperator.isImage(file)) {
                
                ImageInfo imageInfo = new ImageInfo(file);
                imageLayerList.getItems().add(imageInfo);
                addImageViewToStage(imageInfo);
                hasAdd = true;
            }
        }
        if (hasAdd) {
            imageLayerList.save();
        }
    }
    
    private void addImageViewToStage(ImageInfo imageInfo) {
        MyImageView imageView = new MyImageView(imageInfo.getImage(), imageInfo);
        
        
        mainView.getChildren().add(imageView);
        validImageView(imageView);
        
    }
    
    private void validImageView(MyImageView imageView) {
        ImageInfo info = imageView.info;
        double scale = info.getScale();
        if (info.isRelativeScale()) {
            double ratioX = this.selectDeviceInfo.width * scale / info.getWidth();
            double ratioY = this.selectDeviceInfo.height * scale / info.getHeight();
            scale = Math.min(ratioX, ratioY);
        }
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
        
        imageView.setLayoutX((getScreenWidth() * info.getHPercent() - (imageView.getRealWidth() / 2)));
        imageView.setLayoutY((getScreenHeight() * info.getVPercent() - (imageView.getRealHeight() / 2)));
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
        
        imageLayerListDragDropped(dragEvent);
    }
    
    public void saveStage(MouseEvent event) {
        imageLayerList.save();
    }
    
    public void onStagePressed(MouseEvent event) {
        if (event.isControlDown()) {
            stage.setCursor(Cursor.MOVE);
            Point2D stagePos = stage.sceneToLocal(event.getSceneX(), event.getSceneY());
            _selectMainViewPressPos = new Point2D(stagePos.getX() - mainView.getLayoutX(), stagePos.getY() - mainView.getLayoutY());
            return;
        }
        _selectMainViewPressPos = null;
        ObservableList<Node> children = mainView.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            if (!(child instanceof MyImageView)) {
                continue;
            }
            MyImageView view = (MyImageView) child;
            Point2D point2D = view.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (!view.isTransparent(point2D)) {
                selectImage(view.info);
                _selectImageMousePressPos = _selectImage.sceneToLocal(event.getSceneX(), event.getSceneY());
                return;
            }
            
        }
        _selectImageMousePressPos = null;
        selectImage(null);
    }
    
    public void onStageDragged(MouseEvent event) {
        if (event.isControlDown()) {
            if (_selectMainViewPressPos != null) {
                Point2D stageMousePos = stage.sceneToLocal(event.getSceneX(), event.getSceneY());
                mainView.setLayoutX(stageMousePos.getX() - _selectMainViewPressPos.getX());
                mainView.setLayoutY(stageMousePos.getY() - _selectMainViewPressPos.getY());
            }
            return;
        }
        stage.setCursor(Cursor.DEFAULT);
        if (_selectImage == null) {
            return;
        }
        if (_selectImageMousePressPos == null) {
            return;
        }
        double imageWidth = _selectImage.getRealWidth();
        double imageHeight = _selectImage.getRealHeight();
        Point2D mainViewMousePos = mainView.sceneToLocal(event.getSceneX(), event.getSceneY());
        double valueX = mainViewMousePos.getX() - _selectImageMousePressPos.getX();
        double mainViewWidth = getScreenWidth();
        double mainViewHeight = getScreenHeight();
        valueX = Math.min(valueX, mainViewWidth - imageWidth / 2);
        valueX = Math.max(valueX, -imageWidth / 2);
        _selectImage.setLayoutX(valueX);
        double valueY = mainViewMousePos.getY() - _selectImageMousePressPos.getY();
        valueY = Math.min(valueY, mainViewHeight - imageHeight / 2);
        valueY = Math.max(valueY, -imageHeight / 2);
        _selectImage.setLayoutY(valueY);
        
        _selectImage.info.setHPercent((_selectImage.getLayoutX() + imageWidth / 2) / mainViewWidth);
        _selectImage.info.setVPercent((_selectImage.getLayoutY() + imageHeight / 2) / mainViewHeight);
        hPercentComponent.setPercent(_selectImage.info.getHPercent());
        vPercentComponent.setPercent(_selectImage.info.getVPercent());
        
    }
    
    
    public void onStageMouseMove(MouseEvent event) {
        stage.setCursor(Cursor.DEFAULT);
    }
    
    public void onStageMouseRelease(MouseEvent event) {
        stage.setCursor(Cursor.DEFAULT);
    }
}

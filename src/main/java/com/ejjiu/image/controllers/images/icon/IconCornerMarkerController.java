package com.ejjiu.image.controllers.images.icon;

import com.ejjiu.image.componet.fxml.ChoiceBoxComponent;
import com.ejjiu.image.enums.ConfigType;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/23 17:28
 */
public class IconCornerMarkerController implements ChangeListener<Number> {
    public ChoiceBoxComponent<CornerMarkData> connerMarkerTypeCb;
    public ChoiceBoxComponent<CornerMarkData> connerMarkerColorCb;
    private Runnable changeCallback;
    
    public void init(Runnable changeCallback) {
        this.changeCallback = changeCallback;
        ObservableList<CornerMarkData> typeDataList = FXCollections.observableArrayList();
        typeDataList.add(new CornerMarkData("无", ""));
        typeDataList.add(new CornerMarkData("BETA", "beta"));
        typeDataList.add(new CornerMarkData("ALPHA", "alpha"));
        typeDataList.add(new CornerMarkData("测试版", "test"));
        connerMarkerTypeCb.setItems(typeDataList);
        
        
        ObservableList<CornerMarkData> colorDataList = FXCollections.observableArrayList();
        colorDataList.add(new CornerMarkData("浅色", "light"));
        colorDataList.add(new CornerMarkData("深色", "dark"));
        connerMarkerColorCb.setItems(colorDataList);
        
        StringConverter<CornerMarkData> value = new StringConverter<CornerMarkData>() {
            @Override
            public String toString(CornerMarkData object) {
                return object.label;
            }
            
            @Override
            public CornerMarkData fromString(String string) {
                return null;
            }
        };
        connerMarkerColorCb.setConverter(value);
        connerMarkerTypeCb.setConverter(value);
        connerMarkerColorCb.setConfigType(ConfigType.ICON_CONNER_MARKER_COLOR);
        connerMarkerTypeCb.setConfigType(ConfigType.ICON_CONNER_MARKER_TYPE);
        if (connerMarkerColorCb.getSelectionModel().getSelectedIndex() == -1) {
            connerMarkerColorCb.getSelectionModel().select(0);
        }
        if (connerMarkerTypeCb.getSelectionModel().getSelectedIndex() == -1) {
            connerMarkerTypeCb.getSelectionModel().select(0);
        }
        connerMarkerTypeCb.getSelectionModel().selectedIndexProperty().addListener(this);
        connerMarkerColorCb.getSelectionModel().selectedIndexProperty().addListener(this);
    }
    
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        Platform.runLater( this.changeCallback);
    
    }
    
    public static class CornerMarkData {
        private final String label;
        public final String data;
        
        public CornerMarkData(String label, String data) {
            this.label = label;
            this.data = data;
        }
    }
}

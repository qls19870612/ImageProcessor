package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/23 16:32
 */
public class ToggleGroupComponent extends ToggleGroup implements AutowireInterface, ChangeListener<Toggle> {
    
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    
    public ToggleGroupComponent() {
        super();
        this.initialize();
        this.selectedToggleProperty().addListener(this);
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        int index = configRepository.getInt(configType, -1);
        if (index != -1) {
            //刚开始设置的时候，toggle还没有加到此group,需要延后再设置
            Platform.runLater(() -> {
                if (index >= getToggles().size()) {
                    return;
                }
                selectToggle(getToggles().get(index));
            });
            
        }
        
    }
    
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    
    @Override
    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (configRepository == null || configType == null) {
            return;
        }
        configRepository.setInt(configType, getToggles().indexOf(newValue));
    }
}

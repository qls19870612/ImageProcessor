package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;


/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/14 9:38
 */
public class CheckBoxComponent extends CheckBox implements AutowireInterface, ChangeListener<Boolean> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    
    public CheckBoxComponent() {
        super();
        this.selectedProperty().addListener(this);
        this.initialize(null,null);
        
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        int config = configRepository.getInt(configType);
        setSelected(config == 1);
    }
    
    public final void setSelectedWithSave(boolean value) {
        this.setSelected(value);
        configRepository.setInt(configType, value ? 1 : 0);
    }
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (configRepository == null) {
            return;
        }
        configRepository.setInt(configType, newValue ? 1 : 0);
    }
}

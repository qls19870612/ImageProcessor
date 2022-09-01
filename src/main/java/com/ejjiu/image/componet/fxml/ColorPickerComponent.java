package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/22 10:56
 */
public class ColorPickerComponent extends ColorPicker  implements AutowireInterface, ChangeListener<Color> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    public ColorPickerComponent() {
        super();
        this.valueProperty().addListener(this);
        this.initialize();
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null) {
            return;
        }
        String config = configRepository.getConfig(configType);
        
        setSelectedWithSave(config);
    }
    
    public final void setSelectedWithSave(String hexValue) {
        if (StringUtils.isEmpty(hexValue)) {
            return;
        }
        Color web = Color.web(hexValue);
        this.valueProperty().set(web);
        configRepository.setConfig(configType,web.toString());
    }
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
 
    @Override
    public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
        configRepository.setConfig(configType,newValue.toString());
    }
}

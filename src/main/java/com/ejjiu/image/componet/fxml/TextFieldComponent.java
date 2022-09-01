package com.ejjiu.image.componet.fxml;


import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/23 14:15
 */
public class TextFieldComponent extends TextField implements AutowireInterface, ChangeListener<String> {
    private SimpleObjectProperty<ConfigType> configType;
    @Autowired
    private ConfigRepository configRepository;
    
    public String getDefaultText() {
        return defaultText;
    }
    
    private final String defaultText;
    
    public TextFieldComponent(@NamedArg(value = "text", defaultValue = "") String text) {
        super(text);
        defaultText = text;
        this.initialize();
        textProperty().addListener(this);
    }
    
    private SimpleObjectProperty<ConfigType> getConfigTypeProperty() {
        if (configType == null) {
            configType = new SimpleObjectProperty<>(TextFieldComponent.this, "configType", null);
        }
        return configType;
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.getConfigTypeProperty().set(configType);
        if (configRepository == null) {
            return;
        }
        String config = configRepository.getConfig(configType);
        if (StringUtils.isNotEmpty(config)) {
            
            setText(config);
            return;
        }
        if (StringUtils.isNotEmpty(defaultText)) {
            setText(defaultText);
            return;
        }
        setText(config);
        
    }
    
    
    @FXML
    public ConfigType getConfigType() {
        return this.getConfigTypeProperty().get();
    }
    
    
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (configRepository == null) {
            return;
        }
        
        configRepository.setConfig(configType.get(), newValue);
    }
}

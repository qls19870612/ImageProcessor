package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/23 17:42
 */
public class ChoiceBoxComponent<T> extends ChoiceBox<T> implements AutowireInterface, ChangeListener<Number> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    public ChoiceBoxComponent(@NamedArg(value = "configType") ConfigType configType) {
        super();
        this.getSelectionModel().selectedIndexProperty().addListener(this);
        this.initialize();
        setConfigType(configType);
    }
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null || configType == null) {
            return;
        }
        int config = configRepository.getInt(configType);
        this.getSelectionModel().select(config);
        
    }
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (configRepository==null || configType == null) {
            return;
        }
        configRepository.setInt(configType,newValue.intValue());
    }
}

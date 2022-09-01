package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;


/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/14 9:50
 */
public class ComboboxComponent<T> extends ComboBox<T> implements AutowireInterface, ChangeListener<Number> {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    public ComboboxComponent() {
        super();
        this.getSelectionModel().selectedIndexProperty().addListener(this);
        this.initialize();
    }
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository==null) {
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
        configRepository.setInt(configType,newValue.intValue());
    }
}

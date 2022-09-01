package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;


/**
 *
 * 创建人  liangsong
 * 创建时间 2019/10/24 18:03
 */
public class InputComponent extends AbstractInputComponent implements AutowireInterface {
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;

    public InputComponent() {
        super();
    }


    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        String config = configRepository.getConfig(configType);
        if (StringUtils.isNotEmpty(config)) {
            textField.setText(config);
        }
        button.setText("清除");
        textField.setPromptText("");
    }

    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }

    public void setLabel(String value) {
        label.setText(value);
    }

    public String getLabel() {
        return label.getText();
    }

    public String getInputText() {
        return textField.getText();
    }

    public void setInputText(String value) {
        textField.setText(value);
    }
    public void setInputTextWithSave(String value) {
        textField.setText(value);
        configRepository.setConfig(configType.name(), value);
    }

    @Override
    protected void onBtnClick(MouseEvent mouseEvent) {

        textField.setText("");
        configRepository.setConfig(configType.name(), "");

    }

    @Override
    protected void onTextChange(String oldValue, String newValue) {
        if (configRepository == null) {
            return;
        }
        configRepository.setConfig(configType.name(), newValue);
    }
}

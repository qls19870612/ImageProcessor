package com.ejjiu.image.componet.fxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/10/24 17:53
 */
public abstract class AbstractInputComponent extends HBox implements ChangeListener<String>, EventHandler<MouseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractInputComponent.class);
    @FXML
    public Button button;
    @FXML
    public TextField textField;
    @FXML
    public Label label;

    public int getLabelWidth() {
        return labelWidth;
    }

    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
        label.setMinWidth(labelWidth);
        label.setMaxWidth(labelWidth);
    }
    public int getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(int inputWidth) {
        this.inputWidth = inputWidth;
        this.textField.setMinWidth(inputWidth);
        this.textField.setMaxWidth(inputWidth);
    }
    private int labelWidth = 0;
    private int inputWidth = 0;

    public AbstractInputComponent() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/InputComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setLabelWidth(80);
        textField.textProperty().addListener(this);
        button.setOnMouseClicked(this);
    }


    protected void onBtnClick(MouseEvent mouseEvent) {

    }

    protected void onTextChange(String oldValue, String newValue) {

    }

    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        onTextChange(oldValue, newValue);
    }

    @Override
    public void handle(MouseEvent event) {
        onBtnClick(event);
    }


    //    @FXML
    //    public void changeText(Event event) {
    //        logger.debug("changeText inputMethodEvent:{}", event);
    //    }
}

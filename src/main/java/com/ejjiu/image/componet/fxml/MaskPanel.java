package com.ejjiu.image.componet.fxml;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/10/26 18:21
 */
public class MaskPanel extends AnchorPane implements Initializable {
    @FXML
    public TextArea textArea;

    public MaskPanel() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/MaskPanel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        AnchorPane.setLeftAnchor(textArea, 0d);
        AnchorPane.setRightAnchor(textArea, 0d);
        AnchorPane.setTopAnchor(textArea, 0d);
        AnchorPane.setBottomAnchor(textArea, 0d);
        textArea.setEditable(false);

    }

    public void append(String info) {
        if (textArea.getLength() > 0) {
            textArea.appendText("\n");
        }
        textArea.appendText(info);
    }

    public void clear() {
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}

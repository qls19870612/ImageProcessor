package com.ejjiu.image.componet;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InputBox {

    private InputBoxCallback callback;

    public InputBox display(String title, String message, InputBoxCallback callback) {
        this.callback = callback;
        Stage window = new Stage();
        window.setTitle(title);
        //modality要使用Modality.APPLICATION_MODEL
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(150);

        Button button = new Button("确 定");


        Label label = new Label(message);
        TextField inputTF = new TextField();
        inputTF.setMaxWidth(window.getMinWidth() - 120);
        VBox layout = new VBox(10);

        layout.getChildren().addAll(label, inputTF, button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
      
        window.setScene(scene);
        //使用showAndWait()先处理这个窗口，而如果不处理，main中的那个窗口不能响应
        button.setOnAction(e -> {
            close(callback, window, inputTF);
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                close(callback, window, inputTF);
            }
        });


        window.showAndWait();
        return this;
    }

    private void close(InputBoxCallback callback, Stage window, TextField inputTF) {
        window.close();
        if (callback != null) {
            callback.onSure(inputTF.getText());
        }
    }

    public static InputBox showAlert(String title, String message, InputBoxCallback callback) {

        return new InputBox().display(title, message, callback);
    }

    public static void showAlert(String message, InputBoxCallback callback) {
        if (Platform.isFxApplicationThread()) {
            showAlert("警告", message, callback);
        } else {

            Platform.runLater(() -> showAlert("警告", message, callback));
        }
    }

    public interface InputBoxCallback {
        void onSure(String text);
    }
}

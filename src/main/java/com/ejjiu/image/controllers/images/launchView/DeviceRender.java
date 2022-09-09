package com.ejjiu.image.controllers.images.launchView;


import com.ejjiu.image.controllers.images.launchView.vo.DeviceInfo;
import com.ejjiu.common.events.ItemEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/10 10:19
 */
public class DeviceRender<T extends DeviceInfo> extends ListCell<T> {
    private static final Logger logger = LoggerFactory.getLogger(DeviceRender.class);
    
    private final HBox hbox;
    private final ImageView image;
 
    private final ListView<String> list;
    
    public DeviceRender() {
        super();
        hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(0);
        image = new ImageView();
        image.setFitWidth(30);
        image.setFitHeight(60);
        
        list = new ListView<>();
        list.setStyle("-fx-padding: 0;-fx-background-color: #00000000;-fx-border-color: #00000000;-fx-hbar-policy: never;" +
                "-fx-vbar-policy: never;" );
        list.setMaxWidth(160);
        list.setMouseTransparent(true);
//        this.setStyle("-fx-background-color: 0");
        list.setCellFactory(param -> new LabelRender());
        list.setMaxHeight(70);
        list.setPrefHeight(30);
        
        hbox.getChildren().addAll(image,list);
        
        setMaxWidth(160);
        this.setOnMousePressed(event -> {
            getListView().fireEvent(new ItemEvent<T>(ItemEvent.ITEM_SELECT,getItem()));
        });
    }
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            return;
        }
        image.setImage(item.getImageComponent());
//        logger.debug("updateItem item.name:{}", item.name);
//        String value = item.name.replaceAll("\\n", "\n");
//        logger.debug("updateItem value:{}", value);
        list.getItems().clear();
        list.getItems().addAll(item.name.split("\\n"));
        list.getItems().add(item.width + " x " + item.height);
        list.setPrefHeight(list.getItems().size() * LabelRender.HEIGHT);
        setGraphic(hbox);
//        setStyle("-fx-background-color: #00000000");
    
    }
    public static class LabelRender extends ListCell<String>
    {
        private final Label label;
        public static int HEIGHT = 22;
        public LabelRender() {
            super();
            this.label = new Label();
           
            this.setHeight(HEIGHT);
     
        }
    
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-background-color: #00000000;");
            if (item == null || empty) {
                setGraphic(null);
                return;
            }
     
            label.setText(item);
            setGraphic(label);
        }
    }
}

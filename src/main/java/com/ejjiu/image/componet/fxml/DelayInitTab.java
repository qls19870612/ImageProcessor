package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.controllers.AbstractController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/09/02 19:42
 */
public class DelayInitTab extends Tab {
    private static final Logger logger = LoggerFactory.getLogger(DelayInitTab.class);
    @Getter
    @Setter
    private String source;

    private boolean inited;
    private AbstractController controller;

    public DelayInitTab() {
        super();
    }

    public void initView() {
        FXMLLoader loader;
        try {
            URL resource = new URL(source);
            loader = new FXMLLoader(resource);
            loader.load();
        } catch (Exception e) {
            logger.error("initView source:{}", source);
            e.printStackTrace();
            return;
        }
        if (!(loader.getController() instanceof AbstractController)) {
            throw new RuntimeException("Source:" + source + " controller 必需是sample.fxml.controllers.AbstractController");
        }
        controller = loader.getController();
        Parent root = loader.getRoot();
        this.setContent(root);
    }

    public void onSelect() {

        if (!inited) {
            inited = true;
            initView();

            controller.setup();

        }
        controller.onSelect();



    }

    public void onAppClose() {
        if (controller != null) {
            controller.onAppClose();
        }
    }


}

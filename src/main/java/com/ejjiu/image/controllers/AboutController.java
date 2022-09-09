package com.ejjiu.image.controllers;

import com.ejjiu.common.controllers.AbstractController;
import com.ejjiu.common.boot.Main;

import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/30 15:05
 */
public class AboutController extends AbstractController {
    public void onLinkClick(MouseEvent event) {
        Hyperlink source = (Hyperlink) event.getSource();
        Main.hostServices.showDocument(source.getText());
       
    
    }
}

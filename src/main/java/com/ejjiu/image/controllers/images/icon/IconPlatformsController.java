package com.ejjiu.image.controllers.images.icon;

import com.google.common.collect.Lists;

import com.ejjiu.image.componet.fxml.CheckBoxComponent;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/18 12:13
 */
public class IconPlatformsController {
    public HBox root;
    
    public List<String> selectPlatforms()
    {
        List<String> list = Lists.newArrayList();
        for (Node child : root.getChildren()) {
            CheckBoxComponent checkBoxComponent = (CheckBoxComponent) child;
            if (!checkBoxComponent.isSelected()) {
                continue;
            }
            list.add((String) checkBoxComponent.getUserData());
        }
        return list;
    }
}

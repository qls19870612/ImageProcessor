package com.ejjiu.image.controllers.images;

import com.ejjiu.image.ConfigTypeOfImage;
import com.ejjiu.common.controllers.AbstractTabController;

import javafx.scene.control.TabPane;
/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/18 9:41
 */
public class ImageController extends AbstractTabController {
    public TabPane tabPanel;
    
    @Override
    public void setup() {
        super.setup();
        setup(tabPanel, ConfigTypeOfImage.IMAGE_RELATE_INDEX);
    }
}

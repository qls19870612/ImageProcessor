package com.ejjiu.image;

import com.ejjiu.common.boot.Main;
import com.ejjiu.common.enums.ConfigType;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/09/05 12:00
 */
public class ImageMain extends Main {
    @Override
    public void init() throws Exception {
        ConfigType.initExtends(ConfigTypeOfImage.class);
        super.init();
    }
    
    public static void main(String[] args) {
        splashScreen = new SplashScreenExt();
        savedArgs = args;
        launch(args);
    }
}

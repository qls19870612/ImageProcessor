package com.ejjiu.image.interfaces;

import javafx.scene.control.Tab;
 

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/03/28 15:26
 */
public interface ITab extends AutowireInterface {
    ITab setTab(Tab tab);
    Tab getTab();
    void onSelect();

    void onAppClose();
}

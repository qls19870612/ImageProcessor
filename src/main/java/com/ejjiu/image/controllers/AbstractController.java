package com.ejjiu.image.controllers;

import com.ejjiu.image.interfaces.ITab;

import javafx.scene.control.Tab;


/**
 *
 * 创建人  liangsong
 * 创建时间 2020/11/13 16:30
 */
public abstract class AbstractController implements ITab {
    private Tab tab;

    @Override
    public Tab getTab() {
        return tab;
    }

    @Override
    public ITab setTab(Tab tab) {
        this.tab = tab;
        return this;
    }

    /**
     * 只执行一次
     */
    public void setup(){

    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onAppClose() {

    }
}

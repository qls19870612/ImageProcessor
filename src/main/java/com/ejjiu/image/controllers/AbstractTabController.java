package com.ejjiu.image.controllers;

import com.ejjiu.image.componet.fxml.DelayInitTab;
import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.jpa.ConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;


/**
 *
 * 创建人  liangsong
 * 创建时间 2021/04/30 10:28
 */
public abstract class AbstractTabController extends AbstractController {
    private TabPane tab;
    private ConfigType selectIndex;
    @Autowired
    private ConfigRepository configRepository;
    private ArrayList<DelayInitTab> tabs = new ArrayList<>();


    @Override
    public void setup() {


    }

    protected void initSubTab(TabPane tabPanel)
    {

        for (Tab tab : tabPanel.getTabs()) {
            if (tab instanceof DelayInitTab) {
                addSubTab((DelayInitTab) tab);
            }
        }
    };



    protected void setup(TabPane tabPanel, ConfigType selectIndex)
    {
        this.tab = tabPanel;
        initSubTab(tabPanel);
        this.selectIndex = selectIndex;
        int tabIndex = configRepository.getInt(selectIndex);
        tabPanel.getSelectionModel().select(tabIndex);
        if (tabPanel.getSelectionModel().getSelectedIndex() != -1) {
            Tab selectedItem = tabPanel.getSelectionModel().getSelectedItem();
            for (DelayInitTab tab : tabs) {
                if(tab == selectedItem)
                {
                    tab.onSelect();
                    break;
                }
            }
        }
        tabPanel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for (DelayInitTab tab : tabs) {
                if (tab == newValue) {
                    tab.onSelect();
                    return;
                }
            }

//            throw new RuntimeException("未处理的Ios Tab:" + newValue.getText());

        });
    }
    protected void addSubTab(DelayInitTab tab)
    {
        if (tab == null) {
            return;
        }
        this.tabs.add(tab);
    }
    @Override
    public void onAppClose() {
        for (DelayInitTab tab : tabs) {
            tab.onAppClose();
        }
        if (tab ==null) {
            return;
        }
        configRepository.setInt(selectIndex, tab.getSelectionModel().getSelectedIndex());
    }

}

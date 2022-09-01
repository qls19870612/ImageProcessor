package com.ejjiu.image.controllers.images.icon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.ejjiu.image.componet.fxml.TextFieldComponent;
import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;
import com.ejjiu.image.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/19 19:11
 */
public class IconSizesController {
    public HBox sizeBox;
    public TextFieldComponent customImagePrefixTF;
    private TextField textField;
    private ConfigRepository repository;
    
    private void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        this.saveState();
    }
    
    
    public void onAddBtnClick(MouseEvent event) {
        textField = new TextField();
        textField.setPrefWidth(60);
        textField.focusedProperty().addListener(this::textFieldFocusLost);
        textField.setOnKeyPressed(this::textFieldKeyDown);
        sizeBox.getChildren().add(textField);
        textField.requestFocus();
    }
    
    
    public void onResetBtnClick(MouseEvent event) {
        List<ToggleButton> deleteList = new ArrayList<>();
        for (Node child : sizeBox.getChildren()) {
            if (!(child instanceof ToggleButton)) {
                continue;
            }
            ToggleButton toggleButton = (ToggleButton) child;
            if (toggleButton.getUserData() != null) {
                deleteList.add(toggleButton);
            }
            toggleButton.setSelected(false);
            toggleButton.selectedProperty().removeListener(this::changed);
        }
        sizeBox.getChildren().removeAll(deleteList);
        saveState();
    }
    
    public List<Integer> getAllSize() {
        
        List<Integer> list = Lists.newArrayList();
        for (Node child : sizeBox.getChildren()) {
            if (!(child instanceof ToggleButton)) {
                continue;
            }
            ToggleButton toggleButton = (ToggleButton) child;
            String text = toggleButton.getText();
            int value = Utils.safeParseInt(text, 0);
            list.add(value);
            
        }
        return list;
    }
    
    public List<Integer> getSelectSize() {
        
        
        List<Integer> list = Lists.newArrayList();
        for (Node child : sizeBox.getChildren()) {
            if (!(child instanceof ToggleButton)) {
                continue;
            }
            ToggleButton toggleButton = (ToggleButton) child;
            if (!toggleButton.isSelected()) {
                continue;
            }
            String text = toggleButton.getText();
            int value = Utils.safeParseInt(text, 0);
            list.add(value);
            
        }
        return list;
    }
    
    private void textFieldKeyDown(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            textField.getParent().requestFocus();
            
        }
    }
    
    private void textFieldFocusLost(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            sizeBox.getChildren().remove(textField);
            textField.focusedProperty().removeListener(this::textFieldFocusLost);
            int newSize = Utils.safeParseInt(textField.getText(), 0);
            if (newSize <= 0) {
                return;
            }
            for (Node child : sizeBox.getChildren()) {
                if (!(child instanceof ToggleButton)) {
                    continue;
                }
                ToggleButton toggleButton = (ToggleButton) child;
                String text = toggleButton.getText();
                int value = Utils.safeParseInt(text, 0);
                if (value == newSize) {
                    return;
                }
            }
            
            addOneCustomToggleBtn("" + newSize).setSelected(true);
            saveState();
        }
    }
    
    public void init(ConfigRepository repository) {
        
        this.repository = repository;
        String config = repository.getConfig(ConfigType.ICON_SIZES_CUSTOM);
        if (StringUtils.isEmpty(config)) {
            return;
        }
        String[] s = config.split("_");
        
        Set<String> all = Sets.newHashSet(s[0].split(","));
        Set<String> select;
        if (s.length > 1) {
            
            select = Sets.newHashSet(s[1].split(","));
        } else {
            select = Sets.newHashSet();
        }
        List<Integer> allSize = getAllSize();
        for (Integer integer : allSize) {
            all.remove(integer.toString());
        }
        if (all.size() > 0) {
            all.stream().sorted(Comparator.comparingInt(o -> Utils.safeParseInt(o, 0))).forEach(this::addOneCustomToggleBtn);
            
        }
        
        for (Node child : sizeBox.getChildren()) {
            if (!(child instanceof ToggleButton)) {
                continue;
            }
            ToggleButton toggleButton = (ToggleButton) child;
            toggleButton.setSelected(select.contains(toggleButton.getText()));
            toggleButton.selectedProperty().addListener(this::changed);
        }
        
    }
    
    public ToggleButton addOneCustomToggleBtn(String s1) {
        ToggleButton toggleButton = new ToggleButton(s1);
        toggleButton.setTextFill(Color.PURPLE);
        toggleButton.setUserData(s1);
        sizeBox.getChildren().add(toggleButton);
        
        
        toggleButton.selectedProperty().addListener(this::changed);
        return toggleButton;
    }
    
    private void saveState() {
        String all = StringUtils.joinList(getAllSize(), ",");
        String select = StringUtils.joinList(getSelectSize(), ",");
        repository.setConfig(ConfigType.ICON_SIZES_CUSTOM, all + "_" + select);
    }
}

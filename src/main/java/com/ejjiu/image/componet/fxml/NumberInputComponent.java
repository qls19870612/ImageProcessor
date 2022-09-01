package com.ejjiu.image.componet.fxml;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/02/08 9:20
 */
public class NumberInputComponent extends InputComponent {
    private int min = 0;
    private int max = Integer.MAX_VALUE;

    public NumberInputComponent() {
        super();
//        label.setMaxWidth(Region.USE_PREF_SIZE);
//        label.setMinHeight(Region.USE_PREF_SIZE);
//        label.setPrefWidth(30);
//        label.setPrefWidth(Region.USE_PREF_SIZE);

    }

    @Override
    public void setLabelWidth(int labelWidth) {
//        super.setLabelWidth(labelWidth);
    }

    @Override
    protected void onTextChange(String oldValue, String newValue) {
        if (StringUtils.isBlank(newValue)) {
            return;
        }
        String handleValue = newValue;
        if (!newValue.matches("[\\d]*")) {
            handleValue = newValue.replaceAll("[^\\d]", "");
        }
        int num = Integer.parseInt(handleValue);
        if (num > max) {
            num = max;
        }else if (num < min) {
            num = min;
        }
        String showValue = String.valueOf(num);
        textField.setText(showValue);
        super.onTextChange(oldValue, showValue);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    public int getValue(){
        if (StringUtils.isBlank(textField.getText())) {
            return min;
        }
        return Integer.parseInt(textField.getText());
    }
}

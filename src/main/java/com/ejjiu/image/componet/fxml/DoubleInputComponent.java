package com.ejjiu.image.componet.fxml;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/02/08 9:20
 */
public class DoubleInputComponent extends InputComponent {
    private double min = 0;
    private double max = Long.MAX_VALUE;

    public DoubleInputComponent() {
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
        if (!newValue.matches("[\\d.]*")) {
            handleValue = newValue.replaceAll("[^\\d.]", "");
        }
        boolean validRange = false;
        double num;
        try {
            num = Double.parseDouble(handleValue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            super.onTextChange(oldValue, handleValue);
           return;
        }

        if (num > max) {
            num = max;
            validRange = true;
        }else if (num < min) {
            num = min;
            validRange = true;
        }
        if (validRange) {
            handleValue = String.valueOf(num);
            if (handleValue.endsWith(".0")) {
                handleValue = handleValue.substring(0,handleValue.length()-2);
            }
        }
        textField.setText(handleValue);
        super.onTextChange(oldValue, handleValue);
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
    public double getValue(){
        if (StringUtils.isBlank(textField.getText())) {
            return min;
        }
        try {
            return Double.parseDouble(textField.getText());
        }
        catch (Exception e)
        {
            return 0;
        }

    }
}

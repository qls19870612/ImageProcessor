package com.ejjiu.image.componet.fxml;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/02/08 9:20
 */
public class LongInputComponent extends InputComponent {
    private long min = 0;
    private long max = Long.MAX_VALUE;

    public LongInputComponent() {
        super();
//        label.setMaxWidth(Region.USE_PREF_SIZE);
//        label.setMinHeight(Region.USE_PREF_SIZE);
//        label.setPrefWidth(30);
//        label.setPrefWidth(Region.USE_PREF_SIZE);

    }

    @Override
    public void setLabelWidth(int labelWidth) {
        super.setLabelWidth(labelWidth);
    }

    @Override
    protected void onTextChange(String oldValue, String newValue) {
        if (StringUtils.isBlank(newValue)) {
            return;
        }
        long num = Long.parseLong(newValue);
        if (num > max) {
            num = max;
        }else if (num < min) {
            num = min;
        }
        String showValue = String.valueOf(num);
        textField.setText(showValue);
        super.onTextChange(oldValue, showValue);
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
    public long getValue(){
        if (StringUtils.isBlank(textField.getText())) {
            return min;
        }
        return Long.parseLong(textField.getText());
    }
}

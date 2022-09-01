package com.ejjiu.image.componet.fxml;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/18 17:37
 */

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
    
    private final NumberFormat nf;
    private ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();
    private BigDecimal max;
    private BigDecimal min;
    public final BigDecimal getNumber() {
        return number.get();
    }
    
    public final void setNumber(BigDecimal value) {
        if (min != null) {
            value = value.max(min);
        }
        if (max != null) {
            value = value.min(max);
        }
        number.set(value);
    }
    public final void setNumber(String value)
    {
        BigDecimal bigDecimal = new BigDecimal(value);
     
        setNumber(bigDecimal);
    
    }
    
    public ObjectProperty<BigDecimal> numberProperty() {
        return number;
    }
    
    public NumberTextField() {
        this(BigDecimal.ZERO);
    }
    
    public NumberTextField(BigDecimal value) {
        this(value, NumberFormat.getInstance());
        initHandlers();
    }
    
    public NumberTextField(BigDecimal value, NumberFormat nf) {
        super();
        this.nf = nf;
        initHandlers();
        setNumber(value);
    }
    public BigDecimal getMax() {
        return max;
    }
    
    public void setMax(BigDecimal max) {
        this.max = max;
    }
    
    public BigDecimal getMin() {
        return min;
    }
    
    public void setMin(BigDecimal min) {
        this.min = min;
    }
    private void initHandlers() {
        
        // try to parse when focus is lost or RETURN is hit
        setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent arg0) {
                parseAndFormatInput();
            }
        });
        
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue.booleanValue()) {
                    parseAndFormatInput();
                }
                else
                {
                    requestFocus();
                }
                
            }
        });
        
        // Set text in field if BigDecimal property is changed from outside.
        numberProperty().addListener(new ChangeListener<BigDecimal>() {
            
            @Override
            public void changed(ObservableValue<? extends BigDecimal> obserable, BigDecimal oldValue, BigDecimal newValue) {
                setText(nf.format(newValue));
                
            }
        });
    }
    
    /**
     * Tries to parse the user input to a number according to the provided
     * NumberFormat
     */
    private void parseAndFormatInput() {
        try {
            String input = getText();
            if (input == null || input.length() == 0) {
                return;
            }
            Number parsedNumber = nf.parse(input);
            BigDecimal newValue = new BigDecimal(parsedNumber.toString());
            setNumber(newValue);
            setText(nf.format(number.get()));
            selectAll();
        } catch (ParseException ex) {
            // If parsing fails keep old number
            setText(nf.format(number.get()));
        }
    }
}
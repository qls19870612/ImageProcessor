package com.ejjiu.image.componet.fxml;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/18 17:36
 */

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


public class NumberSpinner extends HBox implements AutowireInterface, ChangeListener<String> {
    public static final String ARROW = "NumberSpinnerArrow";
    public static final String NUMBER_FIELD = "NumberField";
    public static final String NUMBER_SPINNER = "NumberSpinner";
    public static final String SPINNER_BUTTON_UP = "SpinnerButtonUp";
    public static final String SPINNER_BUTTON_DOWN = "SpinnerButtonDown";
    private final String BUTTONS_BOX = "ButtonsBox";
    
    public NumberTextField getNumberField() {
        return numberField;
    }
    
    private NumberTextField numberField;
    private ObjectProperty<BigDecimal> stepWidthProperty = new SimpleObjectProperty<>();
    private final double ARROW_SIZE = 3;
    private final Button incrementButton;
    private final Button decrementButton;
    private final NumberBinding buttonHeight;
    private final NumberBinding spacing;
    private Button _mousePressedBtn;
    private Timer timer;
    
    
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    
    public NumberSpinner(@NamedArg(value = "value", defaultValue = "0") BigDecimal value,
            @NamedArg(value = "stepWidth", defaultValue = "1") BigDecimal stepWidth,
            @NamedArg(value = "totalWidth", defaultValue = "100") int totalWidth, @NamedArg(value = "max") BigDecimal max,
            @NamedArg(value = "min") BigDecimal min) {
        this(value, stepWidth, NumberFormat.getInstance());
        this.setTotalWidth(totalWidth);
        this.setMax(max);
        this.setMin(min);
        
    }
    
    public NumberSpinner(@NamedArg(value = "value", defaultValue = "0") BigDecimal value,
            @NamedArg(value = "stepWidth", defaultValue = "1") BigDecimal stepWidth,
            @NamedArg(value = "totalWidth", defaultValue = "100") int totalWidth, @NamedArg(value = "max") BigDecimal max,
            @NamedArg(value = "min") BigDecimal min, @NamedArg(value = "configType") ConfigType configType) {
        this(value, stepWidth, totalWidth, max, min);
        this.numberField.textProperty().addListener(this);
        this.initialize();
        this.setConfigType(configType);
    }
    
    public NumberSpinner(BigDecimal value, BigDecimal stepWidth, NumberFormat nf) {
        super();
        this.setId(NUMBER_SPINNER);
        this.stepWidthProperty.set(stepWidth);
        
        // TextField
        numberField = new NumberTextField(value, nf);
        numberField.setId(NUMBER_FIELD);
        
        // Enable arrow keys for dec/inc
        numberField.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DOWN) {
                decrement();
                //                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.UP) {
                increment();
                //                keyEvent.consume();
            }
        });
        // Painting the up and down arrows
        Path arrowUp = new Path();
        arrowUp.setId(ARROW);
        arrowUp.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0), new LineTo(0, -ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
        // mouse clicks should be forwarded to the underlying button
        arrowUp.setMouseTransparent(true);
        
        Path arrowDown = new Path();
        
        arrowDown.setId(ARROW);
        arrowDown.getElements().addAll(new MoveTo(-ARROW_SIZE, 0), new LineTo(ARROW_SIZE, 0), new LineTo(0, ARROW_SIZE), new LineTo(-ARROW_SIZE, 0));
        arrowDown.setMouseTransparent(true);
        
        // the spinner buttons scale with the textfield size
        // TODO: the following approach leads to the desired result, but it is
        // not fully understood why and obviously it is not quite elegant
        buttonHeight = numberField.heightProperty().subtract(4).divide(2);
        // give unused space in the buttons VBox to the incrementBUtton
        spacing = numberField.heightProperty().subtract(3).subtract(buttonHeight.multiply(2));
        
        // inc/dec buttons
        VBox buttons = new VBox();
        buttons.setId(BUTTONS_BOX);
        incrementButton = new Button();
        incrementButton.setId(SPINNER_BUTTON_UP);
        incrementButton.prefWidthProperty().bind(numberField.heightProperty());
        incrementButton.minWidthProperty().bind(numberField.heightProperty());
        incrementButton.maxHeightProperty().bind(buttonHeight.add(spacing));
        incrementButton.prefHeightProperty().bind(buttonHeight.add(spacing));
        incrementButton.minHeightProperty().bind(buttonHeight.add(spacing));
        incrementButton.setFocusTraversable(false);
        incrementButton.setOnMouseClicked(ae -> {
            increment();
            ae.consume();
        });
        
        
        // Paint arrow path on button using a StackPane
        StackPane incPane = new StackPane();
        incPane.getChildren().addAll(incrementButton, arrowUp);
        incPane.setAlignment(Pos.CENTER);
        
        decrementButton = new Button();
        decrementButton.setId(SPINNER_BUTTON_DOWN);
        decrementButton.prefWidthProperty().bind(numberField.heightProperty());
        decrementButton.minWidthProperty().bind(numberField.heightProperty());
        decrementButton.maxHeightProperty().bind(buttonHeight);
        decrementButton.prefHeightProperty().bind(buttonHeight);
        decrementButton.minHeightProperty().bind(buttonHeight);
        
        decrementButton.setFocusTraversable(false);
        decrementButton.setOnMouseClicked(ae -> {
            decrement();
            ae.consume();
        });
        addListenerBtn(incrementButton);
        addListenerBtn(decrementButton);
        StackPane decPane = new StackPane();
        decPane.getChildren().addAll(decrementButton, arrowDown);
        decPane.setAlignment(Pos.CENTER);
        
        buttons.getChildren().addAll(incPane, decPane);
        this.getChildren().addAll(numberField, buttons);
        numberField.setNumber(value);
        double h = 24;
        this.numberField.setMaxHeight(h);
        this.setHeight(h);
        this.setMinHeight(h);
        this.setMaxHeight(h);
        this.setPrefHeight(h);
        URL resource1 = this.getClass().getResource("/css/NumberSpinner.css");
        String e = resource1.toExternalForm();
        getStylesheets().add(e);
        numberField.textProperty().addListener(this);
    }
    
    private void addListenerBtn(Button button) {
        button.setOnMousePressed(event -> {
            _mousePressedBtn = button;
            startTimer();
        });
        button.setOnMouseReleased(event -> {
            stopTimer();
            _mousePressedBtn = null;
        });
        button.setOnMouseExited(event -> {
            stopTimer();
            _mousePressedBtn = null;
        });
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (_mousePressedBtn == null) {
                        return;
                    }
                    switch (_mousePressedBtn.getId()) {
                        case SPINNER_BUTTON_UP:
                            increment();
                            break;
                        case SPINNER_BUTTON_DOWN:
                            decrement();
                            break;
                    }
                });
                
            }
        }, 500L, 50L);
    }
    
    /**
     * increment number value by stepWidth
     */
    private void increment() {
        BigDecimal value = numberField.getNumber();
        value = value.add(stepWidthProperty.get());
        
        numberField.setNumber(value);
    }
    
    /**
     * decrement number value by stepWidth
     */
    private void decrement() {
        BigDecimal value = numberField.getNumber();
        value = value.subtract(stepWidthProperty.get());
        
        numberField.setNumber(value);
    }
    
    public final void setNumber(BigDecimal value) {
        numberField.setNumber(value);
    }
    public final void setNumber(String value) {
        numberField.setNumber(value);
    }
    
    public ObjectProperty<BigDecimal> numberProperty() {
        return numberField.numberProperty();
    }
    
    public final BigDecimal getNumber() {
        return numberField.getNumber();
    }
    
    public void setTotalWidth(int value) {
        this.numberField.setPrefWidth(value - 26);
        this.setPrefWidth(value);
        
    }
    
    
    // debugging layout bounds
    public void dumpSizes() {
        System.out.println("numberField (layout)=" + numberField.getLayoutBounds());
        System.out.println("buttonInc (layout)=" + incrementButton.getLayoutBounds());
        System.out.println("buttonDec (layout)=" + decrementButton.getLayoutBounds());
        System.out.println("binding=" + buttonHeight.toString());
        System.out.println("spacing=" + spacing.toString());
    }
    
    public BigDecimal getMax() {
        return numberField.getMax();
    }
    
    public void setMax(BigDecimal max) {
        numberField.setMax(max);
    }
    
    public BigDecimal getMin() {
        return numberField.getMin();
    }
    
    public void setMin(BigDecimal min) {
        numberField.setMin(min);
    }
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configRepository == null) {
            return;
        }
        String config = configRepository.getConfig(configType);
        if (StringUtils.isEmpty(config)) {
            return;
        }
        this.numberField.setNumber(config);
        
    }
    
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (configRepository == null) {
            return;
        }
        configRepository.setConfig(configType, newValue);
    }
}
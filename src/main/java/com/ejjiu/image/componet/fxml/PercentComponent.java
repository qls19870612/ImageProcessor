package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.IconUtils;
import com.ejjiu.image.utils.StringUtils;
import com.ejjiu.image.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.transform.TransformChangedEvent;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/11 10:44
 */
public class PercentComponent extends Pane implements AutowireInterface {
    private final int scale;
    private double initPercent = -1;
    private double min;
    private double max;
    private Label titleLabel;
    private Button resetBtn;
    private TextField percentTF;
    private final HBox hBox;
    
    public Slider getSlider() {
        return slider;
    }
    
    private Slider slider;
    
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;
    private static final Logger logger = LoggerFactory.getLogger(PercentComponent.class);
    
    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;
        if (configType != null) {
            int config = configRepository.getInt(configType);
            double percent = config /(double) scale;
            this.slider.setValue(percent);
            this.percentTF.setText(config + "");
        }
        
    }
    
    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }
    
    public PercentComponent(@NamedArg(value = "orientation", defaultValue = "VERTICAL") Orientation orientation,
            @NamedArg(value = "percent", defaultValue = "0") double percent, @NamedArg(value = "min", defaultValue = "0") double min,
            @NamedArg(value = "max", defaultValue = "1") double max, @NamedArg(value = "scale", defaultValue = "100") int scale,
            @NamedArg(value = "suffix", defaultValue = "%") String suffix,
            @NamedArg(value = "configType", defaultValue = "") ConfigType configType) {
        super();
        this.scale = scale;
        
        
        this.titleLabel = new Label();
        
//        Image staticIcon = IconUtils.createStaticIcon("20x20.png");
        Image staticIcon = IconUtils.createStaticIcon("tiny_refresh_plugins.png");
        this.resetBtn = new Button("",new ImageView(staticIcon));
        
//        this.resetBtn.setStyle("-fx-background-image:url(" + staticIcon + ")");
        this.resetBtn.setStyle("-fx-background-insets: 2 2 2 2;");
        this.resetBtn.setMaxSize(18,18);
        this.resetBtn.setOnMouseClicked(event -> {
            reset();
        });
        this.percentTF = new TextField();
        this.percentTF.setPrefWidth(50);
        Label percentLabel;
      
            percentLabel = new Label(suffix);
         
        
        
        hBox = new HBox(5);
        hBox.setAlignment(Pos.BASELINE_LEFT);
        
        hBox.setMinHeight(30);
        
        hBox.getChildren().addAll(resetBtn, percentTF, percentLabel);
        
        
        this.slider = new Slider();
        this.slider.setMax(max);
        this.slider.setMin(min);
        
        
        this.slider.setSnapToPixel(true);
        
        
        this.getChildren().add(hBox);
        this.setOrientation(orientation);
        
        
        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int round = (int) Math.round(newValue.doubleValue() * scale);
            this.percentTF.setText(round + "");
            if (configType != null) {
                this.configRepository.setInt(configType, round);
            }
            
            this.fireEvent(new TransformChangedEvent(this, this));
        });
        this.percentTF.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String handleValue = this.percentTF.getText();
                if (!handleValue.matches("[\\d]*")) {
                    handleValue = handleValue.replaceAll("[^\\d]", "");
                }
                
                int number;
                if (StringUtils.isEmpty(handleValue)) {
                    number = 0;
                } else {
                    number = Utils.safeParseInt(handleValue, 0);
                }
                if (number / (double)scale < this.min) {
                    number = (int) (this.min * scale);
                }
                if (number / (double)scale > this.max) {
                    number = (int) (this.max * scale);
                }
                this.percentTF.setText(number + "");
                this.slider.setValue(number / (double)scale);
                this.fireEvent(new TransformChangedEvent(this, this));
            }
            
        });
        if (configType != null) {
            this.initialize();
        }
        
        initPercent = percent;
        setMin(min);
        setMax(max);
        if (configType != null && configRepository.hasConfig(configType)) {
            
            setConfigType(configType);
        } else {
            setPercent(percent);
        }
        
    }
    
    
    /**
     * The orientation of the {@code ScrollBar} can either be {@link Orientation#HORIZONTAL HORIZONTAL}
     * or {@link Orientation#VERTICAL VERTICAL}.
     */
    private ObjectProperty<Orientation> orientation;
    
    public final void setOrientation(Orientation value) {
        orientationProperty().set(value);
        if (this.slider.getParent() != this) {
            this.getChildren().remove(this.slider);
        } else if (this.slider.getParent() == hBox) {
            hBox.getChildren().remove(this.slider);
        }
        if (value == Orientation.VERTICAL) {
            
            this.slider.setLayoutY(30);
            this.getChildren().add(this.slider);
            this.setMinHeight(46);
        } else {
            int resetBtnIndex = hBox.getChildren().indexOf(resetBtn);
            hBox.getChildren().add(resetBtnIndex, this.slider);
            this.setMinHeight(28);
        }
    }
    
    public final Orientation getOrientation() {
        return orientation == null ? Orientation.VERTICAL : orientation.get();
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        if (orientation == null) {
            orientation = new SimpleObjectProperty<>(PercentComponent.this, "orientation", Orientation.VERTICAL);
        }
        return orientation;
    }
    
    public void setLabel(String label) {
        if (StringUtils.isEmpty(label)) {
            if (titleLabel.getParent() != null) {
                hBox.getChildren().remove(titleLabel);
            }
        } else {
            if (titleLabel.getParent() == null) {
                hBox.getChildren().add(0, titleLabel);
            }
        }
        this.titleLabel.setText(label);
    }
    
    public String getLabel() {
        return this.titleLabel.getText();
    }
    
    public void setMin(double min) {
        this.min = min;
        this.slider.setMin(min);
    }
    
    public void setMax(double max) {
        this.max = max;
        this.slider.setMax(max);
    }
    
    public void setPercent(double percent) {
        
        this.slider.adjustValue(percent);
        int round = (int) Math.round(slider.getValue() * scale);
        this.percentTF.setText(round + "");
        if (configType != null) {
            configRepository.setInt(configType, round);
        }
    }
    
    public void reset() {
        setPercent(initPercent);
    }
    
    public double getPercent() {
        return this.slider.getValue();
    }
    
    public double getMin() {
        return min;
    }
    
    public double getMax() {
        return max;
    }
}

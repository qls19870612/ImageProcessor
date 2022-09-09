package com.ejjiu.image;

import com.ejjiu.common.startLoader.SplashScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/09/05 20:36
 */
public class SplashScreenExt extends SplashScreen {
    private static final Logger logger = LoggerFactory.getLogger(SplashScreenExt.class);
 
    private ImageView imageView;
    private RotateTransition rotateTransition;
    
    @Override
    public Parent getParent() {
        String imagePath = getImagePath();
        URL resource = getClass().getResource(imagePath);
        String url = resource.toExternalForm();
        imageView = new ImageView(url);
        Pane group = new Pane();
        group.setStyle("-fx-background-color: #00000000");
        group.setPrefSize(500,500);
        group.getChildren().add(imageView);
 
        imageView.setSmooth(true);
        double value = (500 - imageView.getImage().getWidth())/2;
        imageView.setX(value);
        imageView.setY(value);
        return group;
    }
    
    @Override
    public void show() {
        super.show();
        rotateTransition = new RotateTransition(Duration.seconds(20),imageView);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setToAngle(360*10);
        rotateTransition.setFromAngle(0);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.play();
    }
    
 
    @Override
    public void hide(Runnable runnable) {
 
        ParallelTransition parallelTransition = new ParallelTransition(imageView);
        parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setToX(5.0);
        scaleTransition.setToY(5.0);
        Duration seconds = Duration.seconds(0.8);
        scaleTransition.setDuration(seconds);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setToValue(0);
        fadeTransition.setDuration(seconds);
        
        parallelTransition.getChildren().addAll(scaleTransition,fadeTransition);
        parallelTransition.setOnFinished(event -> {
            rotateTransition.stop();
            Platform.runLater(runnable);
        });
        parallelTransition.play();
        
    }
    
    @Override
    public String getImagePath() {
        return "/splash/ic_launcher.png";
    }
}

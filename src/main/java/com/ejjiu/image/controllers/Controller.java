package com.ejjiu.image.controllers;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.utils.Utils;
import com.sun.javafx.tk.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

public class Controller extends AbstractTabController {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static int count = 0;



    @FXML
    public Label timeLabel;
    @FXML
    public Label infoLabel;
    @FXML
    public TabPane tabPanel;
    private static Controller instance = null;

    private SimpleDateFormat timeDataFormat;
    public static void logAndPrint(String... args) {
        logger.debug("logAndPrint args:{}", (Object) args);
        log(args);
    }
    public static void log(String... args) {
        if (!Toolkit.getToolkit().isFxUserThread()) {
            Platform.runLater(() -> showLogInFxThread(args));
            return;
        }
        showLogInFxThread(args);
    }


    private static void showLogInFxThread(String[] args) {
        String showTxt = null;
        if (args.length == 1) {
            showTxt = args[0];
        } else if (args.length > 1) {
            StringBuilder stringBuffer = new StringBuilder();
            for (String arg : args) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(">");
                }
                stringBuffer.append(arg);
            }
            showTxt = stringBuffer.toString();
        } else {
            return;
        }
        //        if (showTxt.length() > 70) {
        //            showTxt = showTxt.substring(0, 30) + "...." + showTxt.substring(showTxt.length() - 40);
        //        }
        instance.infoLabel.setText(showTxt);
        if (instance.infoLabel.getTooltip() == null) {

            Tooltip toolTip = new Tooltip();
            Utils.hackTooltipStartTiming(toolTip);
            instance.infoLabel.setTooltip(toolTip);
        }
        instance.infoLabel.getTooltip().setText(showTxt);
        count++;
        instance.timeLabel.setText(count + ": " + instance.timeDataFormat.format(new Date()));
    }



    @Override
    public void setup() {
        super.setup();
        setup(tabPanel, ConfigType.TAB_SELECT_INDEX);

        timeDataFormat = new SimpleDateFormat("HH:mm:ss");
        instance = this;

        infoLabel.setText("");
        timeLabel.setText("");
    }

}

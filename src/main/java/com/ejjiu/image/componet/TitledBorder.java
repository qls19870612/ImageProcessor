package com.ejjiu.image.componet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/07/14 10:07
 */
public class TitledBorder extends StackPane {
    private Label titleLabel = new Label();
    private StackPane contentPane = new StackPane();
 



    public void setContent(Node content)
    {
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);
    }


 


    public void setTitle(String title)
    {
        titleLabel.setText(" " + title + " ");
    }


    public String getTitle()
    {
        return titleLabel.getText();
    }



    public TitledBorder()
    {
        titleLabel.setText("default title");
        titleLabel.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);

        getStyleClass().add("bordered-titled-border");

        StackPane.setMargin(titleLabel,new Insets(0,0,20,8));
        getChildren().addAll(titleLabel, contentPane);
    }

}

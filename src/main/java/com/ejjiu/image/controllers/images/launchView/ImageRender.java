package com.ejjiu.image.controllers.images.launchView;


import com.ejjiu.image.controllers.images.launchView.vo.ImageInfo;
import com.ejjiu.image.events.ItemEvent;
import com.ejjiu.image.file.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/08/09 15:01
 */
public class ImageRender<T extends ImageInfo> extends ListCell<T> {
    private static final Logger logger = LoggerFactory.getLogger(ImageRender.class);
    protected final HBox hbox;
    protected final Label label;
    private final Button button;
    private final ImageView image;
    public static final int HEIGHT = 50;
    private final Border upBorder;
    private final Border downBorder;
    private static final DataFormat IMAGE_INFO = new DataFormat("IMAGE_INFO");
    private  long lastClickTime;
    private long imageClickEnableTime;
    private boolean isUp = true;
    public ImageRender() {
        super();
        hbox = new HBox();
        hbox.setSpacing(5);
        image = new ImageView();
        image.setFitWidth(HEIGHT);
        image.setFitHeight(HEIGHT);
        image.setPreserveRatio(true);
        
        label = new Label("图片");
        
        button = new Button("X");
        
        Region reg = new Region();
        reg.setMinHeight(HEIGHT);
        label.setMaxHeight(HEIGHT);
        
        HBox.setHgrow(reg, Priority.SOMETIMES);
        hbox.getChildren().addAll(image, label, reg, button);
        hbox.setAlignment(Pos.CENTER_LEFT);
        
        upBorder = new Border(new BorderStroke(Color.GREEN, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT, Insets.EMPTY));
        downBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLUE, Color.TRANSPARENT, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT, Insets.EMPTY));
        
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            T item = getItem();
            if (item == null) {
                return;
            }
     
            getListView().fireEvent(new ItemEvent<T>(ItemEvent.ITEM_DELETE, item));
        });
        this.setOnMouseClicked(event -> {
            if (getItem() == null) {
                return;
            }
            getListView().fireEvent(new ItemEvent<T>(ItemEvent.ITEM_SELECT, getItem()));
        });
        this.setOnDragDetected(event -> {
            
            if (getItem() == null) {
                return;
            }
//            logger.debug("ImageRender setOnDragOver getItem.id:{}", getItem().id);
            Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            WritableImage writableImage = new WritableImage((int) getWidth(), (int) getHeight());
            
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(new Color(0f, 0f, 0f, 0.1f));
            this.snapshot(params, writableImage);
            
            content.putImage(writableImage);
            content.put(IMAGE_INFO, getItem().id);
            
            dragboard.setContent(content);
            
            
        });
        this.setOnDragOver(event -> {
            if (getItem() == null) {
                return;
            }
            int id = (int) event.getDragboard().getContent(IMAGE_INFO);
            if (id <=0) {
                return;
            }
           
            isUp = event.getY() < this.getHeight() / 2;
            if (isUp) {
                setBorder(upBorder);
            } else {
                setBorder(downBorder);
            }
            
        });
    
        this.setOnDragExited(event -> {
            T item = getItem();
            if (item == null) {
                return;
            }
            setBorder(null);
            int id = (int) event.getDragboard().getContent(IMAGE_INFO);
            if (id <=0) {
                return;
            }
            
            if (item.id == id) {
                return;
            }
            T moveItem = null;
            for (T t : getListView().getItems()) {
                if (t.id == id) {
                    moveItem = t;
                    break;
                }
            }
            if (moveItem == null) {
                return;
            }
            getListView().getItems().remove(moveItem);
            int i = getListView().getItems().indexOf(item);
 
            if (isUp) {
                getListView().getItems().add(i , moveItem);
                
            } else {
                getListView().getItems().add(i+1, moveItem);
                
            }
            getListView().fireEvent(new ItemEvent<>(ItemEvent.ITEM_SORT,item));
        });
        this.imageClickEnableTime = 0;
        this.image.setOnMouseClicked(event -> {
            if (this.imageClickEnableTime > 0 && System.currentTimeMillis() < this.imageClickEnableTime ) {
                return;
            }
            if (System.currentTimeMillis() - lastClickTime > 500 ) {
                this.lastClickTime = System.currentTimeMillis();
                return;
            }
            this.imageClickEnableTime = System.currentTimeMillis() + 2_000L;
            FileOperator.openFileAndSelect(getItem().getPath());
        });
        setHeight(HEIGHT);
        
    }
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setStyle("");
            return;
        }
        image.setImage(item.getImage());
        label.setText(item.getImage().getWidth() + "x" + item.getImage().getHeight());
        setGraphic(hbox);
        
        
    }
}

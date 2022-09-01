package com.ejjiu.image.componet.fxml;

import com.ejjiu.image.enums.ConfigType;
import com.ejjiu.image.file.FileOperator;
import com.ejjiu.image.interfaces.AutowireInterface;
import com.ejjiu.image.jpa.ConfigRepository;
import com.ejjiu.image.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/10/24 14:13
 */
public class FileSelector extends AbstractInputComponent implements AutowireInterface {
    private static final Logger logger = LoggerFactory.getLogger(FileSelector.class);
    private final Button openFolderBtn;
    private ConfigType configType;
    @Autowired
    private ConfigRepository configRepository;


    private boolean isFolder = false;
    private File file;
    
    public String getExtendStr() {
        return extendStr;
    }
    @FXML
    public void setExtendStr(String extendStr) {
        this.extendStr = extendStr;
    }

    private String extendStr;

    public FileSelector(@NamedArg("configType") ConfigType configType) {
        super();
        openFolderBtn = new Button("打开");
        openFolderBtn.setMinWidth(50);
        this.getChildren().add(2, openFolderBtn);
        openFolderBtn.setOnMouseClicked(this);
        this.setConfigType(configType);
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    @FXML
    public void setConfigType(ConfigType configType) {
        this.configType = configType;

        String config = configRepository.getConfig(configType);
        if (StringUtils.isNotEmpty(config)) {
            textField.setText(config);
        }
    }

    @FXML
    public ConfigType getConfigType() {
        return this.configType;
    }

    public void setLabel(String value) {
        label.setText(value);
    }

    public String getLabel() {
        return label.getText();
    }

    public String getPath() {
        return textField.getText();
    }

    public void setPath(String path) {
        textField.setText(path);
    }
    public void setPathWithSave(String path) {
        setPath(path);
        configRepository.setConfig(configType.name(), path);
    }

    public boolean isExistsDirectory() {
        File file = getFile();
        return file.exists() && file.isDirectory();
    }

    public boolean isExistsFile() {
        File file = getFile();
        return file.exists() && !file.isDirectory();
    }
    public File getFile()
    {
        if (file == null) {
            file = new File(textField.getText());
        }
        return file;
    }
    @Override
    protected void onBtnClick(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == button) {
            onSelectFile();

        } else if (mouseEvent.getSource() == openFolderBtn) {
            if (isFolder) {
                FileOperator.openFile(getPath());
            } else {

                FileOperator.openFileAndSelect(getPath());
            }
        }


    }

    private void onSelectFile() {
        if (isFolder) {
            DirectoryChooser directoryChooser = new DirectoryChooser();

            File oldFile = new File(getPath());
            if (oldFile.exists()) {
                if (!oldFile.isDirectory()) {
                    oldFile = oldFile.getParentFile();
                }
                directoryChooser.setInitialDirectory(oldFile);
            }
            file = directoryChooser.showDialog(textField.getScene().getWindow());
        } else {
            FileChooser fileChooser = new FileChooser();
            if (StringUtils.isNotEmpty(extendStr)) {
                fileChooser.setSelectedExtensionFilter(new ExtensionFilter("请选择(" +extendStr + ")",extendStr.split(",")));
            }
            File oldFile = new File(getPath());
            if (oldFile.exists()) {
                if (!oldFile.isDirectory()) {
                    oldFile = oldFile.getParentFile();
                }
                fileChooser.setInitialDirectory(oldFile);
            }
            file = fileChooser.showOpenDialog(textField.getScene().getWindow());
        }


        if (file != null) {
            textField.setText(getFile().getPath());
            configRepository.setConfig(configType.name(), getFile().getPath());
        }
    }

    @Override
    protected void onTextChange(String oldValue, String newValue) {
        configRepository.setConfig(configType.name(), newValue);
        file = null;
    }

    public String getFileName() {
        if (StringUtils.isEmpty(getPath())) {
            return "";
        }
        return getFile().getName();
    }

    public String getDirectoryAbsolutePath() {
        return getFile().getAbsolutePath();
    }

}

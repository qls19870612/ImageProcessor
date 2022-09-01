package com.ejjiu.image.db;

import com.ejjiu.image.controllers.Controller;
import com.ejjiu.image.db.mysql.MysqlDialectProvider;
import com.ejjiu.image.db.sqlite.SqlLiteDialectProvider;
import com.ejjiu.image.file.FileOperator;
import com.ejjiu.image.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import javafx.collections.ObservableList;


/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/21 11:186
 */

public class EntryCreator {
    private static final Logger logger = LoggerFactory.getLogger(EntryCreator.class);
    private String javaEntryTemplate;
    private AbstractDialectProvider provider;
    public HashMap<String, TableStruct> structHashMap;

    public EntryCreator() {

        javaEntryTemplate = FileOperator.getConfig("config/javaEntryTemplate.template");

    }

    public void initDbStruct(String dbUrl, String dbName, String userName, String password, String dbType) throws Exception {

        switch (dbType) {
            case "Mysql":
                provider = new MysqlDialectProvider();
                break;
            case "Sqlite":

                provider = new SqlLiteDialectProvider();
                break;
            default:
                throw new Exception("未实现的数据库");
        }
        Connection connection = provider.getConnection(dbUrl, userName, password);
        structHashMap = DbUtils.getTableField(connection, dbName);

    }

    public void createEntity(ObservableList<TableStruct> items, String sourceFolder, String packageName, String prefix, String suffix) {
        for (TableStruct tableStruct : items) {
            createEntity(tableStruct, sourceFolder, packageName, prefix, suffix);
        }
        Controller.log("生成成功" + TimeUtils.printTime2(System.currentTimeMillis()));
    }

    public void createEntity(TableStruct tableStruct, String sourceFolder, String packageName, String prefix, String suffix,
            boolean justSelectField) {
        TableField[] fields = tableStruct.fields;

        StringBuilder filedStr = new StringBuilder();
        String importStr = "";

        for (TableField field : fields) {
            if (justSelectField) {
                if (!field.isSelected()) {
                    continue;
                }
            }
            filedStr.append("   private ");
            String javaClass = provider.getJavaClass(field);
            if (importStr.length() != 0 && "Date".equals(javaClass)) {
                importStr = "import java.util.Date";
            }
            filedStr.append(javaClass);
            filedStr.append(" ");
            filedStr.append(field.upLowerFieldName);
            filedStr.append(";");
            filedStr.append(FileOperator.NEXT_LINE);

        }

        String className = prefix + tableStruct.upLowerTableName + suffix;
        String entityContent = javaEntryTemplate.replaceAll("\\$package", packageName);
        entityContent = entityContent.replaceAll("\\$import", importStr);
        entityContent = entityContent.replaceAll("\\$className", className);
        entityContent = entityContent.replaceAll("\\$filed", filedStr.toString());

        FileOperator.writeFile(new File(sourceFolder + "/" + className + ".java"), entityContent);
    }

    public void createEntity(TableStruct tableStruct, String sourceFolder, String packageName, String prefix, String suffix) {
        createEntity(tableStruct, sourceFolder, packageName, prefix, suffix, false);
    }

}

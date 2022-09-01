package com.ejjiu.image.db.sqlite;

import com.ejjiu.image.db.AbstractDialectProvider;
import com.ejjiu.image.db.TableField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/21 11:17
 */
public class SqlLiteDialectProvider extends AbstractDialectProvider {
    private static final Logger logger = LoggerFactory.getLogger(SqlLiteDialectProvider.class);

    @Override
    public Connection getConnection(String url, String user, String password) {


        try {
            return DriverManager.getConnection("jdbc:sqlite:" + url, user, password);
        } catch (SQLException e) {
            logger.debug("getConnection e.getMessage():{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void initTypeMap() {
        addTypeMap("INT", "int");
        addTypeMap("INTEGER", "int");
        addTypeMap("TINYINT", "int");
        addTypeMap("SMALLINT", "int");
        addTypeMap("MEDIUMINT", "int");
        addTypeMap("BIGINT", "int");
        addTypeMap("UNSIGNED BIG INT", "int");
        addTypeMap("NUMERIC", "int");
        addTypeMap("DECIMAL", "int");

        addTypeMap("FLOAT", "float");

        addTypeMap("REAL", "double");
        addTypeMap("DOUBLE", "double");
        addTypeMap("DOUBLE PRECISION", "double");

        addTypeMap("BOOLEAN", "boolean");

        addTypeMap("CHARACTER", "String");
        addTypeMap("VARCHAR", "String");
        addTypeMap("VARYING CHARACTER", "String");
        addTypeMap("NCHAR", "String");
        addTypeMap("NATIVE CHARACTER", "String");
        addTypeMap("NVARCHAR", "String");
        addTypeMap("TEXT", "String");
        addTypeMap("CLOB", "String");

        addTypeMap("BLOB", "byte[]");
        addTypeMap("NO DATATYPE SPECIFIED", "byte[]");

        addTypeMap("DATE", "Date");
        addTypeMap("DATETIME", "Date");

    }

    @Override
    public String getJavaClass(TableField tableField) {

        if (tableField.fieldName.contains("DATE")) {
            return "Date";
        }
        return super.getJavaClass(tableField);
    }


}

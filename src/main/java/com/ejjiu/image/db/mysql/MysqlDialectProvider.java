package com.ejjiu.image.db.mysql;

import com.ejjiu.image.db.AbstractDialectProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/21 11:19
 */
public class MysqlDialectProvider extends AbstractDialectProvider {
    private static final Logger logger = LoggerFactory.getLogger(MysqlDialectProvider.class);

    @Override
    public Connection getConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + url + "?serverTimezone=UTC", user, password);
            //?useUnicode=true&characterEncoding=UTF-8
            //&characterEncoding=UTF-8
            //useUnicode=true
        } catch (SQLException e) {
            logger.debug("getConnection e.getMessage():{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void initTypeMap() {
        addTypeMap("TINYINT", "int");
        addTypeMap("SMALLINT", "int");
        addTypeMap("MEDIUMINT", "int");
        addTypeMap("INT", "int");
        addTypeMap("INTEGER", "int");
        addTypeMap("YEAR", "int");

        addTypeMap("BIGINT", "long");
        addTypeMap("BIGINT UNSIGNED", "long");
        addTypeMap("FLOAT", "float");
        addTypeMap("DOUBLE", "double");
        addTypeMap("DECIMAL", "double");

        addTypeMap("DATE", "Date");
        addTypeMap("TIME", "Date");
        addTypeMap("DATETIME", "Date");
        addTypeMap("TIMESTAMP", "Date");

        addTypeMap("TINYBLOB", "byte[]");
        addTypeMap("BLOB", "byte[]");

        addTypeMap("CHAR", "String");
        addTypeMap("VARCHAR", "String");
        addTypeMap("TINYTEXT", "String");
        addTypeMap("TEXT", "String");
        addTypeMap("MEDIUMBLOB", "String");
        addTypeMap("MEDIUMTEXT", "String");
        addTypeMap("LONGBLOB", "String");
        addTypeMap("LONGTEXT", "String");

        addTypeMap("BIT", "boolean");


    }
}

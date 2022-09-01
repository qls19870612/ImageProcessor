package com.ejjiu.image.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/21 11:14
 */
public abstract class AbstractDialectProvider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDialectProvider.class);

    public abstract Connection getConnection(String url, String user, String password);

    protected HashMap<String, FieldTypeMap> typeMap = new HashMap<>();

    public AbstractDialectProvider() {
        initTypeMap();
    }

    protected abstract void initTypeMap();

    protected void addTypeMap(String dbType, String javaType) {
        FieldTypeMap value = new FieldTypeMap(dbType, javaType);
        typeMap.put(value.dbType, value);
    }

    public String getJavaClass(TableField tableField) {
        FieldTypeMap fieldTypeMap = typeMap.get(tableField.type);
        if (fieldTypeMap == null) {
            logger.warn("getJavaClass tableField:{}", tableField);
            return "Object";
        }
        return fieldTypeMap.javaType;
    }
}

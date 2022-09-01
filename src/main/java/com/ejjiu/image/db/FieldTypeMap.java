package com.ejjiu.image.db;

public class FieldTypeMap {
    public final String dbType;
    public final String javaType;

    public FieldTypeMap(String dbType, String javaType) {
        this.dbType = dbType;
        this.javaType = javaType;
    }
}

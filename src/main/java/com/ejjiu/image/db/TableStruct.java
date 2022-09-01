package com.ejjiu.image.db;

import com.ejjiu.image.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 创建人  liangsong
 * 创建时间 2018/11/17 20:38
 */
public class TableStruct {
    private static final Logger logger = LoggerFactory.getLogger(TableStruct.class);
    public final String upLowerTableName;
    @Getter
    private final String compoundLabel;
    public String tableName;
    public final TableField[] fields;
    private Map<String, TableField> fieldMap;
    @Getter
    @Setter
    private boolean selected = false;

    public TableStruct(String tableName, TableField[] fields) {
        this.tableName = tableName;
        this.fields = fields;
        upLowerTableName = StringUtils.toUpLowerString(tableName, true);
        compoundLabel = tableName + "(" + upLowerTableName + ")";
    }

    public final Map<String, TableField> getFieldMap() {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
            for (TableField field : fields) {
                fieldMap.put(field.fieldName, field);
            }
        }
        return fieldMap;
    }

    @Override
    public String toString() {
        return compoundLabel;
    }
}

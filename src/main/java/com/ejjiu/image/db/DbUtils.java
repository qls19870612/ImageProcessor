package com.ejjiu.image.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * 创建人  liangsong
 * 创建时间 2019/11/21 11:07
 */
public class DbUtils {
    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    public static HashMap<String, TableStruct> getTableField(Connection logDbCon, String logDbName) throws Exception {

        HashMap<String, TableStruct> ret = new HashMap<>();
        DatabaseMetaData metaData = logDbCon.getMetaData();
        ResultSet tableRet = metaData.getTables(logDbName, logDbName, "%", new String[]{"TABLE"});
        /*其中"%"就是表示*的意思，也就是任意所有的意思。其中m_TableName就是要获取的数据表的名字，如果想获取所有的表的名字，就可以使用"%"来作为参数了。*/

        //3. 提取表的名字。
        ArrayList<TableField> fields = new ArrayList<>();
        while (tableRet.next()) {

            String table_name = tableRet.getString("TABLE_NAME");
            String columnName;
            String columnType;
            ResultSet colRet = metaData.getColumns(logDbName, logDbName, table_name, "%");


            fields.clear();

            while (colRet.next()) {

                columnName = colRet.getString("COLUMN_NAME");
                columnType = colRet.getString("TYPE_NAME");
                int dataSize = colRet.getInt("COLUMN_SIZE");
                String remarks = colRet.getString("REMARKS");
                fields.add(new TableField(columnName, columnType, dataSize, remarks));
            }
            colRet.close();
            TableField[] tableFields = fields.toArray(TableField.EMPTY);
            TableStruct tableStruct = new TableStruct(table_name, tableFields);
            ret.put(table_name, tableStruct);
        }
        tableRet.close();
        return ret;
    }

}

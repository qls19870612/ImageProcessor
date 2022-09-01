package com.ejjiu.image.db.sqlite;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/11/19 10:45
 */
public class SQLiteIdentityColumnSupport extends IdentityColumnSupportImpl {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteIdentityColumnSupport.class);
    @Override

    public boolean supportsIdentityColumns() {
        return true;

    }

    @Override
    public boolean supportsInsertSelectIdentity() {
        return true;
    }

    @Override

    public String getIdentitySelectString(String table, String column, int type)

            throws MappingException {
        return "select last_insert_rowid()";

    }


    @Override

    public String getIdentityColumnString(int type) throws MappingException {
        return "not null";

    }
}

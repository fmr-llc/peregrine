package com.alliancefoundry.dao.impl.dbms;

import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Paul Bernard on 12/4/15.
 */
public class EventDBRowMapper {

    protected static String getString(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getString(fieldName);
        }
        return null;
    }

    protected static Integer getInteger(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getInt(fieldName);
        }
        return null;
    }

    protected static Boolean getBoolean(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getBoolean(fieldName);
        }
        return false;
    }

    protected static DateTime getDateTime(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {

        if (columnNames.contains(fieldName)) {

            Object testForNullLong = rs.getObject(fieldName);

            if(testForNullLong!=null){
                Long val = rs.getLong(fieldName);
                return new DateTime(Long.valueOf(val));
            }
        }
        return null;
    }

}

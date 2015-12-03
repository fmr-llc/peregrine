package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.model.Event;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by:
 * Paul Bernard on 11/23/15.
 *
 *
 * The EventRowMapper is used in a variety of jdbcTemplate prepared statements
 * as such it is required that we determine which columns are being returned
 * before calling a named column retrieval if we are to avoid exceptions.
 *
 */
public class EventRowMapper implements RowMapper {

    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        List<String> columnNames = new ArrayList<String>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i < columnCount + 1; i++ ) {
            columnNames.add(rsmd.getColumnName(i));
        }

        Event event = new Event(
                getString(columnNames, rs, "parentId"),
                getString(columnNames, rs, "eventName"),
                getString(columnNames, rs, "objectId"),
                getString(columnNames, rs, "correlationId"),
                getInteger(columnNames, rs, "sequenceNumber"),
                getString(columnNames, rs, "messageType"),
                getString(columnNames, rs, "dataType"),
                getString(columnNames, rs, "source"),
                getString(columnNames, rs, "destination"),
                getString(columnNames, rs, "subdestination"),
                getBoolean(columnNames, rs, "replayIndicator"),
                getDateTime(columnNames, rs, "publishTimestamp"),
                getDateTime(columnNames, rs, "receivedTimestamp"),
                getDateTime(columnNames, rs, "expirationTimestamp"),
                getString(columnNames, rs, "preEventState"),
                getString(columnNames, rs, "postEventState"),
                getBoolean(columnNames, rs, "isPublishable"),
                getDateTime(columnNames, rs, "insertTimestamp")
        );
        event.setEventId(getString(columnNames, rs, "eventId"));

        return event;
    }

    private static String getString(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getString(fieldName);
        }
        return null;
    }

    private static Integer getInteger(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getInt(fieldName);
        }
        return null;
    }

    private static Boolean getBoolean(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            return rs.getBoolean(fieldName);
        }
        return false;
    }

    private static DateTime getDateTime(List<String> columnNames, ResultSet rs, String fieldName) throws SQLException {
        if (columnNames.contains(fieldName)) {
            Long dt = rs.getLong(fieldName);
            if (dt!=null){
                return new DateTime(dt);
            };
        }
        return null;
    }


}

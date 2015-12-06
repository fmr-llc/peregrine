package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.model.Event;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static com.alliancefoundry.dao.impl.dbms.DBConstants.*;

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
public class EventRowMapper extends EventDBRowMapper implements RowMapper  {

    private static final Logger log = LoggerFactory.getLogger(EventRowMapper.class);

    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        List<String> columnNames = new ArrayList<String>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i < columnCount + 1; i++ ) {
            String name = rsmd.getColumnName(i);
            columnNames.add(name);
        }

        Event event = new Event(
                getString(columnNames, rs, PARENT_EVENT_ID.toUpperCase()),
                getString(columnNames, rs, EVENT_NAME.toUpperCase()),
                getString(columnNames, rs, OBJECT_ID.toUpperCase()),
                getString(columnNames, rs, CORRELATION_ID.toUpperCase()),
                getInteger(columnNames, rs, SEQUENCE_NUMBER.toUpperCase()),
                getString(columnNames, rs, MESSAGE_TYPE.toUpperCase()),
                getString(columnNames, rs, DATA_TYPE.toUpperCase()),
                getString(columnNames, rs, SOURCE.toUpperCase()),
                getString(columnNames, rs, DESTINATION.toUpperCase()),
                getString(columnNames, rs, SUBDESTINATION),
                getBoolean(columnNames, rs, REPLAY_INDICATOR.toUpperCase()),
                getDateTime(columnNames, rs, PUBLISH_TIMESTAMP.toUpperCase()),
                getDateTime(columnNames, rs, RECEIVED_TIMESTAMP.toUpperCase()),
                getDateTime(columnNames, rs, EXPIRATION_TIMESTAMP.toUpperCase()),
                getString(columnNames, rs, PREEVENT_STATE.toUpperCase()),
                getString(columnNames, rs, POSTEVENT_STATE.toUpperCase()),
                getBoolean(columnNames, rs, IS_PUBLISHABLE.toUpperCase()),
                getDateTime(columnNames, rs, INSERT_TIMESTAMP.toUpperCase())
        );
        event.setEventId(getString(columnNames, rs, EVENT_ID.toUpperCase()));

        return event;
    }




}

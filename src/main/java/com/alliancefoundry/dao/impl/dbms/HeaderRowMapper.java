package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.model.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import static com.alliancefoundry.dao.impl.dbms.DBConstants.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class HeaderRowMapper extends EventDBRowMapper implements RowMapper {

    private static final Logger log = LoggerFactory.getLogger(HeaderRowMapper.class);

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

        List<String> columnNames = new ArrayList<String>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i < columnCount + 1; i++ ) {
            String name = rsmd.getColumnName(i);
            int type = rsmd.getColumnType(i);
            String label = rsmd.getColumnLabel(i);
            String className = rsmd.getColumnClassName(i);
            columnNames.add(name);

        }

        Triplet nvp = new Triplet(
                getString(columnNames, rs, NAME.toUpperCase()),
                getString(columnNames, rs, VALUE.toUpperCase()),
                null);

        return nvp;
    }


}

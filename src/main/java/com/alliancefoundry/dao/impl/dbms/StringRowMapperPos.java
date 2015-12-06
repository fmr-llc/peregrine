package com.alliancefoundry.dao.impl.dbms;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class StringRowMapperPos implements RowMapper {

    private int pos = 1;

    public StringRowMapperPos(int pos){
        super();
        this.pos = pos;

    }
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        String source = resultSet.getString(pos);
        return source;
    }
}

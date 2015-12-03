package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.model.NVPair;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class HeaderRowMapper implements RowMapper {


    @Override
    public Object mapRow(ResultSet rsHeaders, int i) throws SQLException {

        NVPair nvp = new NVPair(rsHeaders.getString("name"), rsHeaders.getString("value"));

        return nvp;
    }
}

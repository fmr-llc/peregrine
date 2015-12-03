package com.alliancefoundry.dao.impl.dbms;

import com.alliancefoundry.model.DataItem;
import com.alliancefoundry.model.NDIPair;
import com.alliancefoundry.model.PrimitiveDatatype;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Paul Bernard on 11/23/15.
 */
public class PayloadRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rsPayload, int i) throws SQLException {

        String payName = rsPayload.getString("name");
        String payType = rsPayload.getString("dataType");
        String payVal = rsPayload.getString("value");

        PrimitiveDatatype v = null;
        if (payType.equalsIgnoreCase("boolean")) { v = PrimitiveDatatype.Boolean; }
        if (payType.equalsIgnoreCase("byte")) { v = PrimitiveDatatype.Byte; }
        if (payType.equalsIgnoreCase("double")) { v = PrimitiveDatatype.Double; }
        if (payType.equalsIgnoreCase("float")) { v = PrimitiveDatatype.Float; }
        if (payType.equalsIgnoreCase("integer")) { v = PrimitiveDatatype.Integer; }
        if (payType.equalsIgnoreCase("long")) { v = PrimitiveDatatype.Long; }
        if (payType.equalsIgnoreCase("short")) { v = PrimitiveDatatype.Short; }
        if (payType.equalsIgnoreCase("string")) { v = PrimitiveDatatype.String; }
        if (v==null) { v = PrimitiveDatatype.String; }

        NDIPair ndi = new NDIPair(payName, new DataItem(v,payVal));

        return ndi;
    }
}

package com.alliancefoundry.dao.impl.mysql;


import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.dao.impl.dbms.OpenRDBMSDAOImpl;

import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLEventDAOImpl extends OpenRDBMSDAOImpl implements EventDAO {

	public SQLDialect getDialect(){ return SQLDialect.MYSQL; }

	private static final Logger log = LoggerFactory.getLogger(MySQLEventDAOImpl.class);

}

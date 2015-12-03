package com.alliancefoundry.dao.impl.mariadb;

import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.dao.impl.dbms.OpenRDBMSDAOImpl;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MariaDBEventDAOImpl extends OpenRDBMSDAOImpl implements EventDAO {


	public SQLDialect getDialect(){ return SQLDialect.MARIADB; }

	private static final Logger log = LoggerFactory.getLogger(MariaDBEventDAOImpl.class);



}

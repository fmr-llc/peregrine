package com.alliancefoundry.dao.impl.derby;

import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.dao.impl.dbms.OpenRDBMSDAOImpl;
import org.jooq.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmbeddedDerbyEventDAOImpl extends OpenRDBMSDAOImpl implements EventDAO {


	public SQLDialect getDialect(){ return SQLDialect.DERBY; }

	private static final Logger log = LoggerFactory.getLogger(EmbeddedDerbyEventDAOImpl.class);



}

package com.alliancefoundry.dao.impl.postgres;


import com.alliancefoundry.dao.EventDAO;
import com.alliancefoundry.dao.impl.dbms.OpenRDBMSDAOImpl;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresEventDAOImpl extends OpenRDBMSDAOImpl implements EventDAO {

	public SQLDialect getDialect(){ return SQLDialect.POSTGRES;   }

	private static final Logger log = LoggerFactory.getLogger(PostgresEventDAOImpl.class);

}

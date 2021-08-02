package com.nisum.myteam.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nisum.myteam.utils.MyTeamLogger;

@Component 
@Transactional 
public class DbConnection { 
	
	private static Logger log = LoggerFactory.getLogger(DbConnection.class);

	private  Connection connection = null;
	
	@Value("${myTeam.data.mssqldb.url}") 
	private  String url;
	
	@Value("${myTeam.data.mssqldb.username}")
	private  String username;  
	
	@Value("${myTeam.data.mssqldb.password}") 
	private  String password;
	            
	@Value("${myTeam.data.mssqldb.driver}")        
	private  String driver;
 
	public  Connection getDBConnection() throws SQLException {     
		try {     
			
			Class.forName(driver);	    
			connection = DriverManager.getConnection(url,username,password);
			} catch (ClassNotFoundException cnfex) {
			log.error("Problem in loading or " + "registering MS SQL JDBC driver", cnfex);

		}
		return connection;
	}
}
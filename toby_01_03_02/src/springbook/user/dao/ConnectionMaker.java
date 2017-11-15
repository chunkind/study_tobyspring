package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

//new
public interface ConnectionMaker {
	
	public Connection makeConnection() throws ClassNotFoundException, SQLException;

}

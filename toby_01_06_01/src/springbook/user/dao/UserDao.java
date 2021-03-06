package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

public class UserDao{

	//new
	private static UserDao INSTANCE;
	
	private ConnectionMaker connectionMaker;
	
	//old
//	public UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	//new
	private UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	//new
	public static synchronized UserDao getInstance(ConnectionMaker connectionMaker) {
		if(INSTANCE == null) INSTANCE = new UserDao(connectionMaker);
		return INSTANCE;
	}
	
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
		
	}
	
	
	public User get(String id) throws ClassNotFoundException, SQLException{
		
		Connection c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
		
	}
	
}

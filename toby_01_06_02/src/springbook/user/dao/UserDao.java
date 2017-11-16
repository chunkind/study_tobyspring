package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

public class UserDao{

	//old
//	private static UserDao INSTANCE;
	
	private ConnectionMaker connectionMaker;
	//new
	private Connection c;
	private User user;
	
	//old
//	private UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker;
//	}
	//new
	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}
	
	//old
//	public static synchronized UserDao getInstance(ConnectionMaker connectionMaker) {
//		if(INSTANCE == null) INSTANCE = new UserDao(connectionMaker);
//		return INSTANCE;
//	}
	
	
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
		
		//old
//		Connection c = connectionMaker.makeConnection();
		//new
		this.c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		//old
//		User user = new User();
//		user.setId(rs.getString("id"));
//		user.setName(rs.getString("name"));
//		user.setPassword(rs.getString("password"));
		//new
		this.user = new User();
		this.user.setId(rs.getString("id"));
		this.user.setName(rs.getString("name"));
		this.user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return this.user;
		
	}
	
}

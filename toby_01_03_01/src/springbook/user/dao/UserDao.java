package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

//old
//public abstract class UserDao{
public class UserDao{

	//old
//	public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	
	//new
	private SimpleConnectionMaker simpleConnectionMaker;
	
	//new
	public UserDao() {
		simpleConnectionMaker = new SimpleConnectionMaker();
	}
	
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		
		//old
//		Connection c = getConnection();
		//new
		Connection c = simpleConnectionMaker.makeNewConnection();
		
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
//		Connection c = getConnection();
		//new
		Connection c = simpleConnectionMaker.makeNewConnection();
		
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
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDao dao = new UserDao();
		
		User user = new User();
		user.setId("chunkind");
		user.setName("���ؼ�");
		user.setPassword("wnstjd88");
		
		dao.add(user);
		
		System.out.println(user.getId() + " ��� ����");
		User user2 = dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		System.out.println(user2.getId() + " ��ȸ ����");
		
	}
}

//old
//class NUserDao extends UserDao{
//
//	@Override
//	public Connection getConnection() throws ClassNotFoundException, SQLException {
//		//N �� DB connection �ڵ�
//		return null;
//	}
//	
//}

//old
//class DUserDao extends UserDao{
//
//	@Override
//	public Connection getConnection() throws ClassNotFoundException, SQLException {
//		//D �� DB connection �ڵ�
//		return null;
//	}
//	
//}

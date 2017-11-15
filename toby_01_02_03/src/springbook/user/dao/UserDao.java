package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

//old
//public class UserDao {
//new
public abstract class UserDao{

	//old
//	private Connection getConnection() throws ClassNotFoundException, SQLException{
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
//		return c;
//	}
	//new
	public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		
		Connection c = getConnection();
		
		PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
		
	}
	
	
	public User get(String id) throws ClassNotFoundException, SQLException{
		
		Connection c = getConnection();
		
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
		user.setName("김준성");
		user.setPassword("wnstjd88");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		User user2 = dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		System.out.println(user2.getId() + " 조회 성공");
		
	}
}

//new
class NUserDao extends UserDao{

	@Override
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		//N 사 DB connection 코드
		return null;
	}
	
}

//new
class DUserDao extends UserDao{

	@Override
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		//D 사 DB connection 코드
		return null;
	}
	
}

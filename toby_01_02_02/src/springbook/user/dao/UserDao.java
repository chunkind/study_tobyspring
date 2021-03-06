package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

public class UserDao {

	//new : 중복된 코드를 독립적인 메소드로 만들어서 중복을 제거 했다.
	private Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		return c;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		
		//old
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		//new
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
		
		//old
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
		//new
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
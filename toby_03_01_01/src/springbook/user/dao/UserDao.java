package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao{

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	//old
//	public void add(User user) throws ClassNotFoundException, SQLException{
//		
//		Connection c = dataSource.getConnection();
//		
//		PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)");
//		ps.setString(1, user.getId());
//		ps.setString(2, user.getName());
//		ps.setString(3, user.getPassword());
//		
//		ps.executeUpdate();
//		
//		ps.close();
//		c.close();
//		
//	}
	//new
	public void add(User user) throws ClassNotFoundException, SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)");
			ps.setString(1, user.getId());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());
			
			ps.executeUpdate();
		}catch(SQLException e){
			throw e;
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){
				}
			}
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){
				}
			}
		}
		
	}
	
	
	//old
//	public User get(String id) throws ClassNotFoundException, SQLException{
//		
//		Connection c = dataSource.getConnection();
//		
//		PreparedStatement ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
//		ps.setString(1, id);
//		
//		ResultSet rs = ps.executeQuery();
//		
//		User user = null;
//		if(rs.next()){
//			user = new User();
//			user.setId(rs.getString("id"));
//			user.setName(rs.getString("name"));
//			user.setPassword(rs.getString("password"));
//		}
//		
//		rs.close();
//		ps.close();
//		c.close();
//		
//		if(user == null) throw new EmptyResultDataAccessException(1);
//		
//		return user;
//		
//	}
	//new
	public User get(String id) throws ClassNotFoundException, SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			
			c = dataSource.getConnection();
			ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
			ps.setString(1, id);
			
			rs = ps.executeQuery();
			
			User user = null;
			if(rs.next()){
				user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
			
			if(user == null) throw new EmptyResultDataAccessException(1);
			
			return user;
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){
				}
			}
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){
				}
			}
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){
				}
			}
		}
		
		
	}
	
	//old
//	public void deleteAll() throws SQLException{
//		Connection c = dataSource.getConnection();
//		
//		PreparedStatement ps = c.prepareStatement("delete from users");
//		
//		ps.executeUpdate();
//		
//		ps.close();
//		c.close();
//	}
	//new
	public void deleteAll() throws SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		
		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("delete from users");
			ps.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){
				}
			}
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){
				}
			}
		}
		
	}
	
	//old
//	public int getCount() throws SQLException{
//		Connection c = dataSource.getConnection();
//		
//		PreparedStatement ps = c.prepareStatement("select count(*) from users");
//		
//		ResultSet rs = ps.executeQuery();
//		rs.next();
//		int count = rs.getInt(1);
//		
//		rs.close();
//		ps.close();
//		c.close();
//		
//		return count;
//	}
	//new
	public int getCount() throws SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			c = dataSource.getConnection();
			
			ps = c.prepareStatement("select count(*) from users");
			
			
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){
				}
			}
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){
				}
			}
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){
				}
			}
		}
		
	}
	
}

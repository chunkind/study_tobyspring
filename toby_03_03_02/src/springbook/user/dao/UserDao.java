package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import com.mysql.jdbc.Statement;

import springbook.user.domain.User;

public class UserDao{

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	//old
//	public void add(User user) throws ClassNotFoundException, SQLException{
//		StatementStrategy st = new AddStatement(user);
//		jdbcContextWithStatementStrategy(st);
//	}
	//new
	public void add(User user) throws ClassNotFoundException, SQLException{
		jdbcContextWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		});
	}
	
	
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
//		StatementStrategy st = new DeleteAllStatement();
//		jdbcContextWithStatementStrategy(st);
//	}
	//new
	public void deleteAll() throws SQLException{
		jdbcContextWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
				return ps;
			}
		});
	}
	
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
	
	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException{
		Connection c = null;
		PreparedStatement ps = null;
		
		try{
			c = dataSource.getConnection();
			
			ps = stmt.makePreparedStatement(c);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(ps != null) { try { ps.close(); } catch (SQLException e) {} }
			if(c != null) { try { c.close(); } catch (SQLException e) {} }
		}
	}
	
}

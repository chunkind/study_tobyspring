package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import springbook.user.domain.User;

public class UserDao{
	
	private JdbcTemplate jdbcTemplate;

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		this.jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)", user.getId(), user.getName(), user.getPassword());
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
	
	public void deleteAll() throws SQLException{
		//old
//		this.jdbcTemplate.update(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection c) throws SQLException {
//				return c.prepareStatement("DELETE FROM USERS");
//			}
//		});
		//new
		this.jdbcTemplate.update("DELETE FROM USERS");
	}
	
	//old
//	public int getCount() throws SQLException{
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try{
//			c = dataSource.getConnection();
//			
//			ps = c.prepareStatement("select count(*) from users");
//			
//			
//			rs = ps.executeQuery();
//			rs.next();
//			return rs.getInt(1);
//		}catch(SQLException e){
//			throw e;
//		}finally{
//			if(rs != null){
//				try{
//					rs.close();
//				}catch(SQLException e){
//				}
//			}
//			if(ps != null){
//				try{
//					ps.close();
//				}catch(SQLException e){
//				}
//			}
//			if(c != null){
//				try{
//					c.close();
//				}catch(SQLException e){
//				}
//			}
//		}
//		
//	}
	//new
	public int getCount() throws SQLException{
//		return this.jdbcTemplate.query(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("SELECT COUNT(*) FROM USERS");
//			}
//		}, new ResultSetExtractor<Integer>() {
//			@Override
//			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//				rs.next();
//				return rs.getInt(1);
//			}
//		});
		//new2
		return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM USERS");
	}
	
}

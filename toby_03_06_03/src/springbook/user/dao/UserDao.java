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
import org.springframework.jdbc.core.RowMapper;

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
		//old
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		
//		try{
//			
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
//			ps.setString(1, id);
//			
//			rs = ps.executeQuery();
//			
//			User user = null;
//			if(rs.next()){
//				user = new User();
//				user.setId(rs.getString("id"));
//				user.setName(rs.getString("name"));
//				user.setPassword(rs.getString("password"));
//			}
//			
//			if(user == null) throw new EmptyResultDataAccessException(1);
//			
//			return user;
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
		//new
		return this.jdbcTemplate.queryForObject
			( "SELECT * FROM USERS WHERE ID = ?"
			, new Object[] {id}
			, new RowMapper<User>() {
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getString("id"));
					user.setName(rs.getString("name"));
					user.setPassword(rs.getString("password"));
					return user;
				}
			});
	}
	
	public void deleteAll() throws SQLException{
		this.jdbcTemplate.update("DELETE FROM USERS");
	}
	
	public int getCount() throws SQLException{
		return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM USERS");
	}
	
}

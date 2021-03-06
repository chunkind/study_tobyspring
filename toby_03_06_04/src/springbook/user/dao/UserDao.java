package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;

public class UserDao{
	
	private JdbcTemplate jdbcTemplate;

	//old
//	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		//old
//		this.dataSource = dataSource;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		this.jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)", user.getId(), user.getName(), user.getPassword());
	}
	
	
	public User get(String id) throws ClassNotFoundException, SQLException{
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
	
	//new
	public List<User> getAll(){
		return this.jdbcTemplate.query("SELECT * FROM USERS ORDER BY ID",
			new RowMapper<User>() {
				@Override
				public User mapRow(ResultSet rs, int rowNum)
						throws SQLException {
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

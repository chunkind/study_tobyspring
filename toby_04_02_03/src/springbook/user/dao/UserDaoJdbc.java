package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;

//new : UserDao -> UserDaoJdbc 로 인터페이스를 두고 구상클래스로 분리하였다.
public class UserDaoJdbc implements UserDao{

	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<User> userMapper = 
		new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				return user;
			}
		};

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void add(User user){
		this.jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)", user.getId(), user.getName(), user.getPassword());
	}
	
	@Override
	public User get(String id){
		return this.jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE ID = ?", new Object[] {id}, this.userMapper);
	}
	
	@Override
	public List<User> getAll(){
		return this.jdbcTemplate.query("SELECT * FROM USERS ORDER BY ID", this.userMapper);
	}
	
	@Override
	public void deleteAll(){
		this.jdbcTemplate.update("DELETE FROM USERS");
	}
	
	@Override
	public int getCount(){
		return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM USERS");
	}

}

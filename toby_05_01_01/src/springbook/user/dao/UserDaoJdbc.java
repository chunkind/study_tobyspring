package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;

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
				
				//new
				user.setLevel(Level.valueOf(rs.getInt("lv")));
				user.setLogin(rs.getInt("login"));
				user.setRecommend(rs.getInt("recommend"));
				
				return user;
			}
		};

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void add(User user){
		//old
//		this.jdbcTemplate.update("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?, ?, ?)", user.getId(), user.getName(), user.getPassword());
		//new
		this.jdbcTemplate.update("INSERT INTO USERS (id, name, password, lv, login, recommend) VALUES(?, ?, ?, ?, ?, ?)"
				, user.getId(), user.getName(), user.getPassword()
				, user.getLevel().intValue(), user.getLogin(), user.getRecommend());
		
	}
	
	@Override
	public User get(String id){
		return this.jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", new Object[] {id}, this.userMapper);
	}
	
	@Override
	public List<User> getAll(){
		return this.jdbcTemplate.query("SELECT * FROM USERS ORDER BY id", this.userMapper);
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

package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
				user.setLevel(Level.valueOf(rs.getInt("lv")));
				user.setLogin(rs.getInt("login"));
				user.setRecommend(rs.getInt("recommend"));
				return user;
			}
		};
	
	//new
//	private String sqlAdd;
	//new
//	public void setSqlAdd(String sqlAdd) {
//		this.sqlAdd = sqlAdd;
//	}
	//new2 : map사용
	private Map<String, String> sqlMap;
	//new2 : map사용
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void add(User user){
		//old
//		this.jdbcTemplate.update("INSERT INTO USERS (id, name, password, lv, login, recommend) VALUES(?, ?, ?, ?, ?, ?)"
		//new
//		this.jdbcTemplate.update(this.sqlAdd
		//new2
		this.jdbcTemplate.update(this.sqlMap.get("add")
				
				, user.getId(), user.getName(), user.getPassword()
				, user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	@Override
	public User get(String id){
		//old
//		return this.jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", new Object[] {id}, this.userMapper);
		//new
		return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"), new Object[] {id}, this.userMapper);
	}
	
	@Override
	public List<User> getAll(){
		//old
//		return this.jdbcTemplate.query("SELECT * FROM USERS ORDER BY id", this.userMapper);
		//new
		return this.jdbcTemplate.query(this.sqlMap.get("getAll"), this.userMapper);
	}
	
	@Override
	public void deleteAll(){
		//old
//		this.jdbcTemplate.update("DELETE FROM USERS");
		//new
		this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
	}
	
	@Override
	public int getCount(){
		//old
//		return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM USERS");
		//new
		return this.jdbcTemplate.queryForInt(this.sqlMap.get("getCount"));
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
				//old
//				"update users set name = ?, password = ?, lv = ?, login = ?, recommend = ? where id = ? ", 
				//new
				this.sqlMap.get("update"), 
				user.getName(), user.getPassword(),
				user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
				user.getId());
	}

}

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
import springbook.user.sqlservice.SqlService;

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

	//old
//	private Map<String, String> sqlMap;
//	public void setSqlMap(Map<String, String> sqlMap) {
//		this.sqlMap = sqlMap;
//	}
	
	//new
	private SqlService sqlService;
	
	//new
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void add(User user){
		//old
//		this.jdbcTemplate.update(this.sqlMap.get("add")
		//new
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd")
			, user.getId(), user.getName(), user.getPassword()
			, user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	@Override
	public User get(String id){
		//old
//		return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"), new Object[] {id}, this.userMapper);
		//new
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), new Object[] {id}, this.userMapper);
	}
	
	@Override
	public List<User> getAll(){
		//old
//		return this.jdbcTemplate.query(this.sqlMap.get("getAll"), this.userMapper);
		//new
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
	}
	
	@Override
	public void deleteAll(){
		//old
//		this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
		//new
		this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
	}
	
	@Override
	public int getCount(){
		//old
//		return this.jdbcTemplate.queryForInt(this.sqlMap.get("getCount"));
		//new
		return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
	}

	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
			//old
//			this.sqlMap.get("update"), 
			//new
			this.sqlService.getSql("userUpdate"), 
			user.getName(), user.getPassword(),
			user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
			user.getId());
	}

}

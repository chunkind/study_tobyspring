package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory{
	
	@Bean
	public UserDao userDao() {
		//old
//		return new UserDao(connectionMaker());
		//new
		return UserDao.getInstance(connectionMaker());
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
	
}

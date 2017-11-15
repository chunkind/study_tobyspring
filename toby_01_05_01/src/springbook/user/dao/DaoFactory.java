package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//old
//public class DaoFactory {
//new : 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시.
@Configuration
public class DaoFactory{
	
	//old
//	public UserDao userDao() {
	//new : 오브젝트 생성을 담당하는 ioC용 메소드라는 표시.
	@Bean
	public UserDao userDao() {
		return new UserDao(connectionMaker());
	}
	
	//old
//	public ConnectionMaker connectionMaker() {
	//new
	@Bean
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
	
}

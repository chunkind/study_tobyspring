package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//old
//public class DaoFactory {
//new : ���ø����̼� ���ؽ�Ʈ �Ǵ� �� ���丮�� ����� ����������� ǥ��.
@Configuration
public class DaoFactory{
	
	//old
//	public UserDao userDao() {
	//new : ������Ʈ ������ ����ϴ� ioC�� �޼ҵ��� ǥ��.
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

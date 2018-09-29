package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.domain.User;

public class UserDaoTest {
	
		

	//old
//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
	//new
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {

		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		User user = new User();
		user.setId("chunkind");
		user.setName("김준성");
		user.setPassword("wnstjd88");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		User user2 = dao.get(user.getId());
		
		//old
//		if(!user.getName().equals(user2.getName())){
//			System.out.println("테스트 실패(name)");
//		}
//		else if(!user.getPassword().equals(user2.getPassword())){
//			System.out.println("테스트 실패(password)");
//		}
//		else{
//			System.out.println("조회 테스트 성공");
//		}
		//new
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
	
	//new
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.dao.UserDaoTest");
	}
}
package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDaoTest {
	
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {

		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		//new
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		//old
//		User user = new User();
//		user.setId("chunkind");
//		user.setName("김준성");
//		user.setPassword("wnstjd88");
		//new
		User user1 = new User("chunkind", "김준성", "wnstjd88");
		User user2 = new User("momo12", "박정하", "wjdgk93");
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		//old
//		System.out.println(user1.getId() + " 등록 성공");
		//new
		
		//new
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));
		
		//new
		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
	}
	
	//new
	public void count() throws SQLException, ClassNotFoundException{
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user1 = new User("momo02", "박정하", "wjdgk93");
		User user2 = new User("ksb3145", "김새봄", "toqha91");
		User user3 = new User("minjea899", "최민재", "alswo89");
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
		
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException, ClassNotFoundException{
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
	//old
//	public static void main(String[] args) {
//		JUnitCore.main("springbook.user.dao.UserDaoTest");
//	}
}
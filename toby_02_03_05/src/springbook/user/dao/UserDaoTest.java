package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDaoTest {
	
	//new
	private UserDao dao;
	private User user1;
	private User user2;
	private User user3;
	
	//new
	@Before
	public void setUp(){
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		this.dao = context.getBean("userDao", UserDao.class);
		this.user1 = new User("myGirl1", "박정하", "girl1");
		this.user2 = new User("myGirl2", "채수빈", "girl2");
		this.user3 = new User("myGirl3", "한예리", "girl3");
	}
	
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {

		//old
//		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//		UserDao dao = context.getBean("userDao", UserDao.class);
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		//old
//		User user1 = new User("chunkind", "김준성", "wnstjd88");
//		User user2 = new User("momo12", "박정하", "wjdgk93");
		
		dao.add(this.user1);
		assertThat(dao.getCount(), is(1));
		dao.add(this.user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(this.user1.getName()));
		assertThat(userget1.getPassword(), is(this.user1.getPassword()));
		
		User userget2 = dao.get(this.user2.getId());
		assertThat(userget2.getName(), is(this.user2.getName()));
		assertThat(userget2.getPassword(), is(this.user2.getPassword()));
	}
	
	public void count() throws SQLException, ClassNotFoundException{
		//old
//		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//		UserDao dao = context.getBean("userDao", UserDao.class);
		
//		User user1 = new User("momo02", "박정하", "wjdgk93");
//		User user2 = new User("ksb3145", "김새봄", "toqha91");
//		User user3 = new User("minjea899", "최민재", "alswo89");
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(this.user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(this.user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(this.user3);
		assertThat(dao.getCount(), is(3));
		
		
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException, ClassNotFoundException{
		
		//old
//		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//		UserDao dao = context.getBean("userDao", UserDao.class);
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
}
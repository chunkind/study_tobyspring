package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
//old
//@ContextConfiguration(locations="/applicationContext.xml")
//public class UserDaoTest {
//new
//@ContextConfiguration(locations="/applicationContext.xml")
//@DirtiesContext
//public class UserDaoTest {
//new2
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {

	
//	@Autowired
//	private UserDao dao;
	
	//new3
	UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp(){
		this.user1 = new User("myGirl1", "박정하", "girl1");
		this.user2 = new User("myGirl2", "채수빈", "girl2");
		this.user3 = new User("myGirl3", "한예리", "girl3");
		
		//new
//		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "spring", "book", true);
//		dao.setDataSource(dataSource);
		
		//new3
		dao = new UserDao();
		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "spring", "book", true);
		dao.setDataSource(dataSource);
		
	}
	
	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
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
	
	@Test
	public void count() throws SQLException, ClassNotFoundException{
		
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
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
}

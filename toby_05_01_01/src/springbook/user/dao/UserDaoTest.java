package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDaoTest {
	
	@Autowired
	UserDao dao;
	
	@Autowired
	DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp(){
		
		//old
//		this.user1 = new User("myGirl1", "박정하", "girl1");
//		this.user2 = new User("myGirl2", "채수빈", "girl2");
//		this.user3 = new User("myGirl3", "한예리", "girl3");
		//new : 필드 추가
		this.user1 = new User("myGirl1", "박정하", "girl1", Level.BASIC, 1, 0);
		this.user2 = new User("myGirl2", "채수빈", "girl2", Level.SILVER, 55, 10);
		this.user3 = new User("myGirl3", "한예리", "girl3", Level.GOLD, 100, 40);
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
		//old
//		assertThat(userget1.getName(), is(this.user1.getName()));
//		assertThat(userget1.getPassword(), is(this.user1.getPassword()));
		//new
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(this.user2.getId());
		//old
//		assertThat(userget2.getName(), is(this.user2.getName()));
//		assertThat(userget2.getPassword(), is(this.user2.getPassword()));
		//new
		checkSameUser(userget2, user2);
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
	
	@Test
	public void getAll() throws SQLException, ClassNotFoundException {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
		
	}
	
	private void checkSameUser(User user1, User user2){
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		//new
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
	}
	
	@Test(expected=DuplicateKeyException.class)
	public void duplciateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	@Test
	public void sqlExceptionTranslate() {
		
		dao.deleteAll();
		try {
			dao.add(user1);
			dao.add(user1);
		} catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = 
				new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqlEx)
				, is(DuplicateKeyException.class));
		}
		
	}
	
	
}



















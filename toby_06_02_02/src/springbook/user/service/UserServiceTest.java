package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	List<User> users;
	
	static class TestUserService extends UserServiceImpl{
		private String id;
		private TestUserService(String id) {
			this.id = id;
		}
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	static class TestUserServiceException extends RuntimeException{}
	
	static class MockMailSender implements MailSender{
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests(){
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage[] mailMessage) throws MailException {
			
		}
		
	}
	
	//new : test를 위한 UserDaoMock 객체
	static class MockUserDao implements UserDao{
		private List<User> users;
		private List<User> updated = new ArrayList<User>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated(){
			return this.updated;
		}
		@Override
		public List<User> getAll() {
			return this.users;
		}
		@Override
		public void update(User user) {
			updated.add(user);
		}
		@Override
		public void add(User user) {
			throw new UnsupportedOperationException();
		}
		@Override
		public User get(String id) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void deleteAll() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int getCount() {
			throw new UnsupportedOperationException();
		}
	}
	
	@Before
	public void setUp(){
		users = Arrays.asList(
			new User("myGirl01", "박부리", "p1", Level.BASIC, MIN_LOGOUNT_FOR_SILVER-1, 0), 
			new User("myGirl02", "왕부리", "p2", Level.BASIC, MIN_LOGOUNT_FOR_SILVER, 0), 
			new User("myGirl03", "박덕이", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1), 
			new User("myGirl04", "왕덕이", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD), 
			new User("myGirl05", "박고집", "p5", Level.GOLD, 100, Integer.MAX_VALUE) 
		);
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	//old
//	@Test
//	@DirtiesContext
//	public void upgradeLevels() throws Exception{
//		userDao.deleteAll();
//		for(User user : users){
//			userDao.add(user);
//		}
//		
//		MockMailSender mockMailSender = new MockMailSender();
//		userServiceImpl.setMailSender(mockMailSender);
//		
//		userService.upgradeLevels();
//		
//		checkLevelUpgraded(users.get(0), false);
//		checkLevelUpgraded(users.get(1), true);
//		checkLevelUpgraded(users.get(2), false);
//		checkLevelUpgraded(users.get(3), true);
//		checkLevelUpgraded(users.get(4), false);
//		
//		List<String> request = mockMailSender.getRequests();
//		assertThat(request.size(), is(2));
//		assertThat(request.get(0), is(users.get(1).getEmail()));
//		assertThat(request.get(1), is(users.get(3).getEmail()));
//		
//	}
//	private void checkLevelUpgraded(User user, boolean upgraded){
//		User userUpdate = userDao.get(user.getId());
//		if(upgraded){
//			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
//		}
//		else{
//			assertThat(userUpdate.getLevel(), is(user.getLevel()));
//		}
//	}
	//new : mock 객체로 변환
	@Test
	public void upgradeLevels() throws Exception{
		//고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//목 오브젝트로 만든 UserDao를 직접 DI해준다.
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		//MockUserDao로 부터 업데이트 결과를 가져온다.
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "myGirl02", Level.SILVER);
		checkUserAndLevel(updated.get(1), "myGirl04", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	//new
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	
	
	@Test
	public void add(){
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
		
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception{
		
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(mailSender);
		
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		
		for(User user : users) userDao.add(user);
		
		try {
			
			txUserService.upgradeLevels();
			
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {
			
		}
		
		//old
//		checkLevelUpgraded(users.get(1), false);
		//new
		checkUserAndLevel(users.get(1), "myGirl02", Level.BASIC);
	}
	
}

















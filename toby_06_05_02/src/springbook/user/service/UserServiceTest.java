package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
	
	/* new
	 * 같은 타입의 빈이 두 개 존재하기 때문에 필드 이름을 기준으로 주입될 빈이 결정된다. 
	 * 자동 프록시 생성기에 의해 트랜잭션 부가기능이 testUserService 빈에 적용
	 * 됐는지를 확인하는 것이 목적이다.
	 */
	@Autowired
	UserService testUserService;
	
	//old
//	@Autowired
//	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationContext context;
	
	List<User> users;
	
	//old : 포인트컷의 클래스 필터에 선정되도록 이름 변경.
//	static class TestUserService extends UserServiceImpl{
	//new
	static class TestUserServiceImpl extends UserServiceImpl{
		
		//old
//		private String id;
		//new : 테스트 픽스처의 user(3)의 id 값을 고정시켜버렸다.
		private String id = "myGirl04";
		
		//old
//		private TestUserService(String id) {
//			this.id = id;
//		}
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
	
	@Test
	public void upgradeLevels() throws Exception{
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "myGirl02", Level.SILVER);
		checkUserAndLevel(updated.get(1), "myGirl04", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void mockUpgradeLevels() throws Exception{
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg =
			ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
		
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
//	@DirtiesContext //스프링 컨텍스트의 빈 설정을 변경하지 않으므로 @DirtiesContext 애노테이션은 제거됐다. 모든 테스트를 위한 DI 작업은 설정파일을 통해 서버에서 진행되므로 테스트 코드 자체는 단순해진다.
	public void upgradeAllOrNothing() throws Exception{
		
		//old
//		TestUserService testUserService = new TestUserService(users.get(3).getId());
//		testUserService.setUserDao(this.userDao);
//		testUserService.setMailSender(mailSender);
//		
//		ProxyFactoryBean txProxyFactoryBean = 
//			context.getBean("&userService", ProxyFactoryBean.class);
//		txProxyFactoryBean.setTarget(testUserService);
//		UserService txUserService = (UserService) txProxyFactoryBean.getObject();
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			//old
//			txUserService.upgradeLevels();
			//new
			this.testUserService.upgradeLevels();
			
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {
			
		}
		checkUserAndLevel(users.get(1), "myGirl02", Level.BASIC);
	}
	
	//new : 프록시로 변경된 오브젝트인지 확인한다.
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	
}

















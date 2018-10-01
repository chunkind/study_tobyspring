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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	//new : 팩토리 빈을 가져오려면 애플리케이션 컨텍스트가 필요하다.
	@Autowired
	ApplicationContext context;
	
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
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	//Mock 프레임워크를 적용해서 테스트
	@Test
	public void mockUpgradeLevels() throws Exception{
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		//다이내믹한 목 오브젝트 생성과 메소드의 리턴 값 설정.
		//그리고 DI까지 세 줄이면 충분하다.
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		//리턴 값이 없는 메소드를 가진 목 오브젝트는 더욱 간단하게 만들 수 있다.
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		//목 오브젝트가 제공하는 검증 기능을 통해서 어떤 메소드가
		//몇번 호출됐는지. 파라미터는 무엇인지 확인할 수 있다.
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		//파라미터를 정밀하게 검사하기 위해 캡처할 수도 있다.
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
	//new : 다이내믹 프록시 팩토리 빈을 직접 만들어 사용할 때는 없앴다가
	//다시 등장한 컨텍스트 무효화 애노테이션.
	@DirtiesContext
	public void upgradeAllOrNothing() throws Exception{
		
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(mailSender);
		
		//old : 팩토리 빈을 이용하여 생성하는 걸로 변경.
//		TransactionHandler txHandler = new TransactionHandler();
//		txHandler.setTarget(testUserService);
//		txHandler.setTransactionManager(transactionManager);
//		txHandler.setPattern("upgradeLevels");
//		UserService txUserService = (UserService)Proxy.newProxyInstance(
//			getClass().getClassLoader(),
//			new Class[] { UserService.class }, 
//			txHandler);
		//new : 팩토리 빈을 이용하여 생성. 팩토리 빈 자체를 가져와야 하므로 빈 이름에
		//&를 반드시 넣어야 한다.
		TxProxyFactoryBean txProxyFactoryBean =
			context.getBean("&userService", TxProxyFactoryBean.class); //테스트용 타깃 주입
		txProxyFactoryBean.setTarget(testUserService);
		//변경된 타깃 설정을 이용해서 트랜잭션 다이내믹 프록시 오브젝트를 다시 생성.
		UserService txUserService = (UserService) txProxyFactoryBean.getObject();
		
		userDao.deleteAll();
		
		for(User user : users) userDao.add(user);
		
		try {
			
			txUserService.upgradeLevels();
			
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {
			
		}
		
		checkUserAndLevel(users.get(1), "myGirl02", Level.BASIC);
	}
	
}

















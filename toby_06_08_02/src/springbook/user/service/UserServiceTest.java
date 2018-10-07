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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserService testUserService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationContext context;
	
	//new
	@Autowired
	PlatformTransactionManager transactionManager;
	
	List<User> users;

	static class TestUserService extends UserServiceImpl{
		private String id = "myGirl04";
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		@Override
		public List<User> getAll() {
			for(User user: super.getAll()) {
				super.update(user);
			}
			return null;
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
	public void upgradeAllOrNothing() throws Exception{
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}
		catch(TestUserServiceException e) {
			
		}
		checkUserAndLevel(users.get(1), "myGirl02", Level.BASIC);
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	@Test(expected=TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}
	
	//new
//	@Test
//	public void transactionSync() {
//		//트랜잭션 정의는 기본 값을 사용한다.
//		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//		
//		//읽기 전용 트랜잭션으로 정의한다. -> 읽기전용 속성을 위반한 deleteAll에서 실패
//		txDefinition.setReadOnly(true);
//		
//		/*
//		 트랜잭션 매니저에게 트랜잭션을 요청한다. 기존에 시작된 트랜잭션이 없으니 새로운 트랜잭션을 시작 시키고
//		 트랜잭션 정보를 돌려준다. 동시에 만들어진 트랜잭션을 다른 곳에서도 사용할 수 있도록 동기화 한다.
//		 */
//		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);	
//		
//		
//		//아래 3개는 앞에서 만들어진 트랜잭션에 모두 참여한다.
//		userService.deleteAll(); //트랜잭션 참여 테스트(읽기전용)을 위해 이위치에 뒀었음.
////		userDao.deleteAll(); // JdbcTemplate을 통해 이미 시작된 트랜잭션이 있다면 자동으로 참여한다. 따라서 예외가 발생한다.
//		userService.add(users.get(0));
//		userService.add(users.get(1));
//		
//		//앞에서 시작한 트랜잭션을 커밋한다.
//		transactionManager.commit(txStatus);
//	}
	//new2 : 롤백 테스트.
//	@Test
//	public void transactionSync() {
//		//트랜잭션을 롤백했을 때 돌아갈 초기 상태를 만들기 위해 트랜잭션 시작 전에 초기화를 해둔다.
//		userDao.deleteAll();
//		assertThat(userDao.getCount(), is(0));
//		
//		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);	
//		
//		userService.add(users.get(0));
//		userService.add(users.get(1));
//		//userDao의  getCount() 메소드도 같은 트랜잭션에서 동작 한다.
//		//add()에 의해 두 개가 등록됐는지 확인한다.
//		assertThat(userDao.getCount(), is(2)); 
//		
//		//강제로 롤백한다. 트랜잭션 시작 전 상태로 돌아가야 한다.
//		transactionManager.rollback(txStatus);
////		transactionManager.commit(txStatus);
//		
//		//add()의 작업이 취소되고 트랜잭션 시작 이전의 상태임을 확인할 수 있다.
//		assertThat(userDao.getCount(), is(0));
//		
//	}
	//new3 : 롤백테스트2
	@Test
	public void transactionSync() {
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);	

		try {
			//테스트 안의 모든 작업을 하나의 트랜잭션으로 통합한다.
			userService.deleteAll();
			userService.add(users.get(0));
			userService.add(users.get(1));
		}finally {
			/*
			 테스트 결과가 어떻든 상관없이 테스트가 끝나면 무조건 롤백한다.
			 테스트 중에 발생했던 DB의 변경 사항은 모두 이전 상태로 복구된다.
			 */
			transactionManager.rollback(txStatus);
		}
	}
	
}

















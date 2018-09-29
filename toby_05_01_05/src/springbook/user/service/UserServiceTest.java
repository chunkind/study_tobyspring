package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import static springbook.user.service.UserService.MIN_LOGOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	List<User> users;
	
	@Before
	public void setUp(){
		users = Arrays.asList(
			//old 
//			new User("myGirl01", "박정하", "p1", Level.BASIC, 49, 0), 
//			new User("myGirl02", "채수빈", "p2", Level.BASIC, 50, 0), 
//			new User("myGirl03", "한예리", "p3", Level.SILVER, 60, 29), 
//			new User("myGirl04", "나연", "p4", Level.SILVER, 60, 30), 
//			new User("myGirl05", "모모", "p5", Level.GOLD, 100, 100) 
			//new : 상수값 사용.
			new User("myGirl01", "박정하", "p1", Level.BASIC, MIN_LOGOUNT_FOR_SILVER-1, 0), 
			new User("myGirl02", "채수빈", "p2", Level.BASIC, MIN_LOGOUNT_FOR_SILVER, 0), 
			new User("myGirl03", "한예리", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1), 
			new User("myGirl04", "나연", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD), 
			new User("myGirl05", "모모", "p5", Level.GOLD, 100, Integer.MAX_VALUE) 
		);
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels(){
		userDao.deleteAll();
		for(User user : users){
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		//old
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
		//new
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
		
	}
	
	//old
//	private void checkLevel(User user, Level expectedLevel){
//		User userUpdate = userDao.get(user.getId());
//		assertThat(userUpdate.getLevel(), is(expectedLevel));
//	}
	//new
	private void checkLevelUpgraded(User user, boolean upgraded){
		User userUpdate = userDao.get(user.getId());
		if(upgraded){
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		}
		else{
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	@Test
	public void add(){
		userDao.deleteAll();
		
		User userWithLevel = users.get(4); //GOLD 레벨
		User userWithoutLevel = users.get(0); //레벨이 비어있는 사용자
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
		
	}
	
}
package springbook.user.service;

import java.sql.Connection;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	public static final int MIN_LOGOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	//old
//	DataSource dataSource;
	
	//new : DI 사용
	private PlatformTransactionManager transactionManager;
	
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//old
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
	
	//new : DI 사용
	public void setTransactionManager(PlatformTransactionManager transactionManager){
		this.transactionManager = transactionManager;
	}
	
	//old : 로컬 트랜잭션은 버리자
//	public void upgradeLevels() throws Exception{
//		TransactionSynchronizationManager.initSynchronization();
//		Connection c = DataSourceUtils.getConnection(dataSource);
//		c.setAutoCommit(false);
//		
//		try {
//			List<User> users = userDao.getAll();
//			for(User user : users) {
//				if(canUpgradeLevel(user)) {
//					upgradeLevel(user);
//				}
//			}
//			c.commit();
//		}catch(Exception e) {
//			c.rollback();
//			throw e;
//		}finally {
//			DataSourceUtils.releaseConnection(c, dataSource);
//			TransactionSynchronizationManager.unbindResource(this.dataSource);
//			TransactionSynchronizationManager.clearSynchronization();
//		}
//		
//	}
	//new : 다른 db에 트랜잭션을위해 JTA를 사용.
//	public void upgradeLevels() throws Exception{
//		InitialContext ctx = new InitialContext();
//		UserTransaction tx = (UserTransaction)ctx.lookup(USER_TX_JNDI_NAME);
//		
//		tx.begin();
//		Connection c = dataSource.getConnection();
//		
//		try {
//			List<User> users = userDao.getAll();
//			for(User user : users) {
//				if(canUpgradeLevel(user)) {
//					upgradeLevel(user);
//				}
//			}
//			tx.commit();
//		}catch(Exception e) {
//			tx.rollback();
//			throw e;
//		}finally {
//			c.close();
//		}
//		
//	}
	//new2 : JDBC를 이용한 트랜잭션 관리 코드와 글로벌 트랜잭션을 필요한 곳에서는 JTA를 적용하는 문제가 생김 -> 트랜잭션 추상화
//	public void upgradeLevels(){
//		PlatformTransactionManager transactionManager = 
//			new DataSourceTransactionManager(dataSource);
//		
//		//트랜잭션 시작
//		TransactionStatus status = 
//			transactionManager.getTransaction(new DefaultTransactionDefinition());
//		
//		try{
//			List<User> users = userDao.getAll();
//			for(User user : users) {
//				if(canUpgradeLevel(user)) {
//					upgradeLevel(user);
//				}
//			}
//			transactionManager.commit(status);
//		}catch(RuntimeException e){
//			transactionManager.rollback(status);
//			throw e;
//		}
//	}
	//new3 : 스프링 DI를 사용하여 코드 분리 
	public void upgradeLevels(){
		TransactionStatus status = 
			this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try{
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			this.transactionManager.commit(status);
		}catch(RuntimeException e){
			this.transactionManager.rollback(status);
			throw e;
		}
	}
	
	private boolean canUpgradeLevel(User user){
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
	}
	
	public void add(User user){
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
}

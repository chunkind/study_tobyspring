package springbook.user.service;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	public static final int MIN_LOGOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	//new
	DataSource dataSource;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//new
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	//old
//	public void upgradeLevels() {
//		List<User> users = userDao.getAll();
//		for(User user : users) {
//			if(canUpgradeLevel(user)) {
//				upgradeLevel(user);
//			}
//		}
//	}
	//new : 로컬 트잭션 적용.
	public void upgradeLevels() throws Exception{
		//트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
		TransactionSynchronizationManager.initSynchronization();
		//DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
		Connection c = DataSourceUtils.getConnection(dataSource);
		c.setAutoCommit(false);
		
		try {
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			c.commit();
		}catch(Exception e) {
			c.rollback();
			throw e;
		}finally {
			//스프링 유틸리티 메소드를 이용해 DB커넥션을 안전하게 닫는다.
			DataSourceUtils.releaseConnection(c, dataSource);
			//동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
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

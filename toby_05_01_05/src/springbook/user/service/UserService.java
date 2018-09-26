package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	
	//new
	public static final int MIN_LOGOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//old
//	public void upgradeLevels(){
//		List<User> users = userDao.getAll();
//		for(User user : users){
//			Boolean changed = null;
//			
//			//BASIC ���� ���׷��̵� -> SILVER�� ����.
//			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){
//				user.setLevel(Level.SILVER);
//				changed = true;
//			}
//			//SILVER ���� ���׷��̵�  -> GOLD�� ����.
//			else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){
//				user.setLevel(Level.GOLD);
//				changed = true;
//			}
//			//GOLD ���� ���׷��̵� -> �������� �ʴ´�.
//			else if(user.getLevel() == Level.GOLD){
//				changed = false;
//			}
//			else{
//				changed = false;
//			}
//			
//			if(changed){
//				userDao.update(user);
//			}
//		}
//	}
	//new
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	
	//new
//	public boolean canUpgradeLevel(User user) {
//		Level currentLevel = user.getLevel();
//		switch (currentLevel) {
//		case BASIC: return (user.getLogin() >= 50);
//		case SILVER: return (user.getRecommend() >= 30);
//		case GOLD: return false;
//		//���� �������� �ٷ� �� ���� ������ �־����� ���ܸ� �߻���Ų��. ���ο� ������ �߰��ǰ� ������ �������� ������ ������ ���� Ȯ���� �� �ִ�.
//		default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
//		}
//	}
	//new2 : ��� �̿�.
	private boolean canUpgradeLevel(User user){
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	//new
//	private void upgradeLevel(User user) {
//		if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
//		else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
//		userDao.update(user);
//	}
	//new : User ��ü�� ���� ���׷��̵� ����� ���ش�.
	private void upgradeLevel(User user) {
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

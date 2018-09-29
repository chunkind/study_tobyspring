package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels(){
		List<User> users = userDao.getAll();
		for(User user : users){
			Boolean changed = null;
			
			//BASIC 래밸 업그레이드 -> SILVER로 변경.
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){
				user.setLevel(Level.SILVER);
				changed = true;
			}
			//SILVER 래밸 업그레이드  -> GOLD로 변경.
			else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){
				user.setLevel(Level.GOLD);
				changed = true;
			}
			//GOLD 래밸 업그레이드 -> 변경하지 않는다.
			else if(user.getLevel() == Level.GOLD){
				changed = false;
			}
			else{
				changed = false;
			}
			
			if(changed){
				userDao.update(user);
			}
		}
	}
	
	//new
	public void add(User user){
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
}
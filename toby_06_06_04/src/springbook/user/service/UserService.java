package springbook.user.service;

import java.util.List;

import springbook.user.domain.User;

public interface UserService{
	void add(User user);
	void upgradeLevels();
	
	//new :트랜잭션을 위해 추가.
	User get(String id);
	List<User> getAll();
	void deleteAll();
	void update(User user);
	
}
package springbook.user.dao;

import java.util.List;

import springbook.user.domain.User;

public interface UserDao{
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	//new : 수정 추가
	void update(User user);
}

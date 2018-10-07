package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

//old
//public interface UserService{
//	void add(User user);
//	void upgradeLevels();
//	User get(String id);
//	List<User> getAll();
//	void deleteAll();
//	void update(User user);
//}
//new
/*
 <tx:method name="" />과 같은 설정 효과를 가져온다. 메소드 레벨 @Transactional 애노테이션이 없으므로
 대체 정책에 따라 타입 레벨에 부여된 디폴트 속성이 적용 된다.
 */
@Transactional
public interface UserService{
	void add(User user);
	void upgradeLevels();
	void update(User user);
	void deleteAll();
	
	/*
	 <tx:method name="get*" read-only="true" />를 애노테이션 방식으로 변경한 것이다.
	 메소드 단위로 부여된 트랜잭션의 속성이 타입 레벨에 부여된 것에 우선해서 적용된다. 같은 속성
	 을 가졌어도 메소드 레벨에 부여될 때는 메소드마다 반복될 수밖에 없다.
	 */
	@Transactional(readOnly=true)
	List<User> getAll();
	@Transactional(readOnly=true)
	User get(String id);
}
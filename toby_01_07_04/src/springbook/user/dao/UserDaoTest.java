package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

public class UserDaoTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		//�̱��� �׽�Ʈ
		DaoFactory factory = new DaoFactory();
		UserDao dao1 = factory.userDao();
		UserDao dao2 = factory.userDao();
		System.out.println(dao1);
		System.out.println(dao2);
		
		//�̱��� �׽�Ʈ
		UserDao dao3 = context.getBean("userDao", UserDao.class);
		UserDao dao4 = context.getBean("userDao", UserDao.class);
		System.out.println(dao3);
		System.out.println(dao4);
		
		
		User user = new User();
		user.setId("chunkind");
		user.setName("���ؼ�");
		user.setPassword("wnstjd88");
		
		dao.add(user);
		
		System.out.println(user.getId() + " ��� ����");
		User user2 = dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		System.out.println(user2.getId() + " ��ȸ ����");
	}
	
}

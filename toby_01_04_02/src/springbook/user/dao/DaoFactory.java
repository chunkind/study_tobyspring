package springbook.user.dao;

public class DaoFactory {
	
	public UserDao userDao() {
		//old
//		ConnectionMaker connectionMaker = new DConnectionMaker();
//		UserDao userDao = new UserDao(connectionMaker);
//		return userDao;
		
		//new
//		return new UserDao(new DConnectionMaker());
		
		//new2
		return new UserDao(connectionMaker());
	}
	
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
	
}

package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserServiceImpl implements UserService{
	public static final int MIN_LOGOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	private MailSender mailSender;
	
	//old : 트랜잭션 추출
//	private PlatformTransactionManager transactionManager;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setMailSender(MailSender mailSender){
		this.mailSender = mailSender;
	}
	
	//old : 트랜잭션 추출
//	public void setTransactionManager(PlatformTransactionManager transactionManager){
//		this.transactionManager = transactionManager;
//	}
	
	//old
//	@Override
//	public void upgradeLevels(){
//		TransactionStatus status = 
//			this.transactionManager.getTransaction(new DefaultTransactionDefinition());
//		
//		try{
//			upgradeLevelsInternal();
//			this.transactionManager.commit(status);
//		}catch(RuntimeException e){
//			this.transactionManager.rollback(status);
//			throw e;
//		}
//	}
//	public void upgradeLevelsInternal() {
//		List<User> users = userDao.getAll();
//		for(User user : users) {
//			if(canUpgradeLevel(user)) {
//				upgradeLevel(user);
//			}
//		}
//	}
	//new : 원상복구후 트랜잭션 제거
	@Override
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
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
		sendUpgradeEMail(user);
	}
	
	@Override
	public void add(User user){
		if(user.getLevel() == null){
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
	
	private void sendUpgradeEMail(User user) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");
		
		this.mailSender.send(mailMessage);
	}
}

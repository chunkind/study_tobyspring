package springbook;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.jdbc.Driver;

import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="springbook.user")
@Import(SqlServiceContext.class)
//new
@PropertySource("/database.properties")
public class AppContext {
	
	@Configuration
	@Profile("production")
	public static class ProductionAppContext {
		@Bean
		public MailSender mailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("localhost");
			return mailSender;
		}
	}
	
	@Configuration
	@Profile("test")
	public static class TestAppContext {
		@Autowired UserDao userDao;
		@Bean
		public UserService testUserService() {
			return new TestUserService();
		}
		@Bean
		public MailSender mailSender() {
			return new DummyMailSender();
		}
	}
	
	//old
//	@Bean
//	public DataSource dataSource() {
//		//mysql
//		SimpleDriverDataSource ds = new SimpleDriverDataSource();
//		ds.setDriverClass(Driver.class);
//		ds.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
//		ds.setUsername("spring");
//		ds.setPassword("book");
//
//		//oracle
////		SimpleDriverDataSource ds = new SimpleDriverDataSource();
////		ds.setDriverClass(OracleDriver.class);
////		ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
////		ds.setUsername("spring");
////		ds.setPassword("book");
//		
//		return ds;
//	}
	
	//new
//	@Autowired
//	Environment env;
//	@Bean
//	public DataSource dataSource() {
//		SimpleDriverDataSource ds = new SimpleDriverDataSource();
//		
//		try {
//			ds.setDriverClass((Class<? extends java.sql.Driver>)
//				Class.forName(env.getProperty("db.driverClass")));
//		}catch(ClassNotFoundException e) {
//			throw new RuntimeException(e);
//		}
//		ds.setUrl(env.getProperty("db.url"));
//		ds.setUsername(env.getProperty("db.username"));
//		ds.setPassword(env.getProperty("db.password"));
//		
//		return ds;
//	}
	
	//new2
	@Value("${db.driverClass}") Class<? extends Driver> driverClass;
	@Value("${db.url}") String url;
	@Value("${db.username}") String username;
	@Value("${db.password}") String password;
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(this.driverClass);
		ds.setUrl(this.url);
		ds.setUsername(this.username);
		ds.setPassword(this.password);
		return ds;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	
}





















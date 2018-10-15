package springbook;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.driver.OracleDriver;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTest.TestUserService;
import springbook.user.sqlservice.OxmSqlService;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

@Configuration
@EnableTransactionManagement
//new:auto 스캔을위해 필요
@ComponentScan(basePackages="springbook.user")
public class TestApplicationContext {
	
	@Bean
	public DataSource dataSource() {
		//mysql
//		SimpleDriverDataSource ds = new SimpleDriverDataSource();
//		ds.setDriverClass(Driver.class);
//		ds.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
//		ds.setUsername("spring");
//		ds.setPassword("book");

		//oracle
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(OracleDriver.class);
		ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
		ds.setUsername("spring");
		ds.setPassword("book");
		
		return ds;
	}
	
	@Bean
	public DataSource embeddedDatabase() {
		return new EmbeddedDatabaseBuilder()
			.setName("embeddedDatabase")
			.setType(HSQL)
			.addScript("classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
			.build();
	}
	
	//old
//	@Bean
//	public UserDao userDao() {
//		//old
////		UserDaoJdbc dao = new UserDaoJdbc();
////		dao.setDataSource(dataSource());
////		dao.setSqlService(sqlService());
////		return dao;
//		//new : Autowired로 변경
//		return new UserDaoJdbc();
//	}
	//new
	@Autowired
	UserDao userDao;
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	
//	@Bean
//	public UserService userService() {
//		UserServiceImpl service = new UserServiceImpl();
//		
//		//old
////		service.setUserDao(userDao());
//		//new
//		service.setUserDao(this.userDao);
//		
//		service.setMailSender(mailSender());
//		return service;
//	}

	@Bean
	public UserService testUserService() {
		TestUserService testService = new TestUserService();
		
		//old
//		testService.setUserDao(userDao());
		//new
		testService.setUserDao(this.userDao);
		
		testService.setMailSender(mailSender());
		return testService;
	}

	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
	
	@Bean
	public SqlService sqlService() {
		OxmSqlService sqlService = new OxmSqlService();
		sqlService.setUnmarshaller(unmarshaller());
		sqlService.setSqlRegistry(sqlRegistry());
		return sqlService;
	}
	
	@Bean
	public SqlRegistry sqlRegistry() {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		sqlRegistry.setDataSource(embeddedDatabase());
		return sqlRegistry;
	}

	@Bean
	public Unmarshaller unmarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("springbook.user.sqlservice.jaxb");
		return marshaller;
	}
	
}




















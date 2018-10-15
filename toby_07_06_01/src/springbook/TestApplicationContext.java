package springbook;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

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

//new : XML -> POJO 방식으로 설정파일 전환 
@Configuration
//@ImportResource("/test-applicationContext.xml")
public class TestApplicationContext {
	
	//new
//	@Autowired
//	private SqlService sqlService;

	/*
	 XML -> POJO로 변환
	 <bean id="dataSource" class="org.springframework.jdbc.datasource.SimpleDriverDataSource">
		<property name="driverClass" value="com.mysql.jdbc.Driver" />
		<property name="url"  value="jdbc:mysql://localhost/springbook" />
		<property name="username"  value="spring" />
		<property name="password"  value="book" />
	 </bean>
	 */
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
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	
	@Bean
	public UserDao userDao() {
		UserDaoJdbc dao = new UserDaoJdbc();
		dao.setDataSource(dataSource());
		//new
//		dao.setSqlService(this.sqlService);
		//new
		dao.setSqlService(sqlService());
		return dao;
	}
	
	@Bean
	public UserService userService() {
		UserServiceImpl service = new UserServiceImpl();
		service.setUserDao(userDao());
		service.setMailSender(mailSender());
		return service;
	}

	@Bean
	public UserService testUserService() {
		TestUserService testService = new TestUserService();
		testService.setUserDao(userDao());
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
	
	//new
//	@Resource EmbeddedDatabase embeddedDatabase;
	//new 2
	@Bean
	public DataSource embeddedDatabase() {
		return new EmbeddedDatabaseBuilder()
			.setName("embeddedDatabase")
			.setType(HSQL)
			.addScript("classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
			.build();
	}

	@Bean
	public SqlRegistry sqlRegistry() {
		EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
		//new
//		sqlRegistry.setDataSource(this.embeddedDatabase);
		//new
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




















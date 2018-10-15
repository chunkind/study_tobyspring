package springbook;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.jdbc.Driver;

import oracle.jdbc.driver.OracleDriver;
import springbook.user.dao.UserDao;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTest.TestUserService;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages="springbook.user")
@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class})
public class AppContext {
	
	@Bean
	public DataSource dataSource() {
		//mysql
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(Driver.class);
		ds.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
		ds.setUsername("spring");
		ds.setPassword("book");

		//oracle
//		SimpleDriverDataSource ds = new SimpleDriverDataSource();
//		ds.setDriverClass(OracleDriver.class);
//		ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
//		ds.setUsername("spring");
//		ds.setPassword("book");
		
		return ds;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager tm = new DataSourceTransactionManager();
		tm.setDataSource(dataSource());
		return tm;
	}
	
}





















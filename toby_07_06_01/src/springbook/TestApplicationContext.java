package springbook;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import oracle.jdbc.driver.OracleDriver;

//new : XML -> POJO 방식으로 설정파일 전환 
@Configuration
@ImportResource("/test-applicationContext.xml")
public class TestApplicationContext {

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
		ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
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
	
}

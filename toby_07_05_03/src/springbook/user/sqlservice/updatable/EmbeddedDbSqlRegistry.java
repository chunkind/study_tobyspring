package springbook.user.sqlservice.updatable;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
	SimpleJdbcTemplate jdbc;
	//new
	//JdbcTemplate과 트랜잭션을 동기화해주는 트랜잭션 템플릿이다.
	//멀티스레드 환경에서 공유 가능핟.
	TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		jdbc = new SimpleJdbcTemplate(dataSource);
		//new
		/*
		 dataSource로 TransactionManager를 만들고 이를 이용해
		 TransactionTemplate을 생성한다.
		 */
		transactionTemplate = new TransactionTemplate(
			new DataSourceTransactionManager(dataSource));
	}
	
	public void registerSql(String key, String sql) {
		jdbc.update("insert into sqlmap(key_, sql_) values(?,?)", key, sql);
	}

	public String findSql(String key) throws SqlNotFoundException {
		try {
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
		}
		catch(EmptyResultDataAccessException e) {
			throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다", e);
		}
	}

	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?" , sql, key);
		if (affected == 0) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
		}
	}

	//old
//	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
//		for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
//			updateSql(entry.getKey(), entry.getValue());
//		}
//	}
	//new
	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			//트랜잭션 템플릿이 만드는 트랜잭션 경계 안에서 동작 할 코드를 콜백 형태로 만들고 TransactionTemplate의
			//execute() 메소드에 전달한다.
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
					updateSql(entry.getKey(), entry.getValue());
				}
			}
		});
	}
}

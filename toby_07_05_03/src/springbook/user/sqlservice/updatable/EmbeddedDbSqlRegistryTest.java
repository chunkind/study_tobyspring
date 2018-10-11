package springbook.user.sqlservice.updatable;

import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
	EmbeddedDatabase db;
	
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder()
			.setType(HSQL)
			.addScript("classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
			.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}

	//new
	@Test
	public void transactionalUpdate() {
		/*
		 초기 상태를 확인한다. 이미 슈퍼클래스의 다른 테스트 메소드에서
		 확인하긴 했지만 트랜잭션 롤백 후의 결과와 비교돼서 이 테스트의
		 목적인 롤백 후의 상태는 처음과 동일하다는 것을 비교해서 보여주려
		 고 넣었다.
		 */
		checkFindResult("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		//에러발생  롤백이 되었는지 확인해보자
		sqlmap.put("KEY9999!@#$", "Modified9999");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			//에러가발생하지 않으면 안된다. 그럴경우 테스트 실패처리.
			fail();
		}
		catch(SqlUpdateFailureException e) {}
		
		//롤백되었으니 초기상태로 와야함.
		checkFindResult("SQL1", "SQL2", "SQL3");
	}
	
}

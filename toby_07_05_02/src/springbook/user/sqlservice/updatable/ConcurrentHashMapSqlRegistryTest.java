package springbook.user.sqlservice.updatable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

//new
//public class ConcurrentHashMapSqlRegistryTest {
//	UpdatableSqlRegistry sqlRegistry;
//	
//	@Before
//	public void setUp() {
//		sqlRegistry = new ConcurrentHashMapSqlRegistry();
//		/*
//		 각 테스트 메소드에서 사용할 초기 SQL 정보를 미리 등록해둔다.
//		 */
//		sqlRegistry.registerSql("KEY1", "SQL1");
//		sqlRegistry.registerSql("KEY2", "SQL2");
//		sqlRegistry.registerSql("KEY3", "SQL3");
//	}
//	
//	@Test
//	public void find() {
//		checkFindResult("SQL1", "SQL2", "SQL3");
//	}
//
//	private void checkFindResult(String expected1, String expected2, String expected3) {
//		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
//		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
//		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
//	}
//	
//	@Test(expected=SqlNotFoundException.class)
//	public void unknownKey() {
//		/*
//		 주어진 키에 해당하는 SQL을 찾을 수 없을 때
//		 예외가 발생하는지를 확인한다. 예외상황에 대한
//		 테스트는 빼먹기가 쉽기 때문에 항상 의식적으로
//		 넣으려고 노력해야 한다.
//		 */
//		sqlRegistry.findSql("SQL9999!@#$");
//	}
//	
//	@Test
//	public void updateSingle() {
//		/*
//		 하나의 SQL을 변경하는 기능에 대한 테스트다.
//		 검증할 때는 변경된 SQL 외의 나머지 SQL은
//		 그대로인지도 확인해주는게 좋다.
//		 */
//		sqlRegistry.updateSql("KYE2", "Modified2");
//		checkFindResult("SQL1", "Modified2", "SQL3");
//	}
//	
//	@Test
//	public void updateMulti() {
//		//한 번에 여러 개의 SQL을 수정하는 기능을 검증한다.
//		Map<String, String> sqlmap = new HashMap<String, String>();
//		sqlmap.put("KEY1", "Modified1");
//		sqlmap.put("KEY3", "Modified3");
//		
//		sqlRegistry.updateSql(sqlmap);
//		checkFindResult("modified1", "SQL2", "Modified3");
//	}
//	
//	@Test(expected=SqlUpdateFailureException.class)
//	public void updateWithNotExistingKey() {
//		//존재하지 않는 키의 SQl을 변경하려고 시도할 때 예외가 발생하는 것을 검증한다.
//		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
//	}
//}

//new2 : AbstractUpdatableSqlRegistryTest 구현
public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	}
}
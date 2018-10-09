package springbook.user.sqlservice;

import java.util.Map;

//new
public interface UpdatableSqlRegistry extends SqlRegistry{

	public void updateSql(String key, String sql) throws SqlUpdateFailureException;
	
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
	
}

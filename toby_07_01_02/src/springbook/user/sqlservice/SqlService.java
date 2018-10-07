package springbook.user.sqlservice;

//new
public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
}

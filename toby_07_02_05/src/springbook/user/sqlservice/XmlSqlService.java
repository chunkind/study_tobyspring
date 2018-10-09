package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

//old
//public class XmlSqlService implements SqlService{
//new
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
	private Map<String, String> sqlMap = new HashMap<String, String>();
	private String sqlmapFile;
	
	//new : 의존 오브젝트를 DI 받을 수 있도록 인터페이스 타입의 프로퍼티로 선언해둔다.
	private SqlReader sqlReader;
	
	//new
	private SqlRegistry sqlRegistry;
	
	//new
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	//new
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	@PostConstruct
	public void loadSql() {
		//old
//		String contextPath = Sqlmap.class.getPackage().getName();
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
//			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		}catch (JAXBException e) {
//			throw new RuntimeException(e);
//		}
		//new
		this.sqlReader.read(this.sqlRegistry);
	}
	

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		//old
//		String sql = sqlMap.get(key);
//		if(sql == null) {
//			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
//		}else {
//			return sql;
//		}
		//new
		try {
			return this.sqlRegistry.findSql(key);
		}catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

	//new : SqlRegistry 구현
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}else {
			return sql;
		}
	}
	
	//new : SqlRegistry 구현
	/*
	 HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독될 수 있도록 인터페이스의 메소드로 접근하게 해준다.
	 */
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	//new : SqlReader 구현
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(sqlmapFile);
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		}catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}

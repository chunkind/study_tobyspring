package springbook.user.sqlservice;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

//new
public class JaxbXmlSqlReader implements SqlReader{
	//굳이 상수로 만들지 않고 바로 sqlmapFile의 값으로 넣어도 상관없지만 이렇게 해주면 의도가 코드에 분명히 드러나고 코드도 폼이 난다.
	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
	
	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	//sqlmapFile 프로퍼티를 지정하면 지정된 파일이 사용되고, 아니라면 디폴트로 넣은 파일이 사용된다.
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

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

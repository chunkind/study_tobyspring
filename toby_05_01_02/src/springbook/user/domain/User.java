package springbook.user.domain;

public class User {
	
	String id;
	String name;
	String password;
	
	//new
	Level level;
	int login;
	int recommend;
	
	public User(){}
	
	//old
//	public User(String id, String name, String password){
//		this.id = id;
//		this.name = name;
//		this.password = password;
//	}
	//new : 생성자 추가.
	public User(String id, String name, String password, Level level, 
		int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	//new : get,set ter 추가.
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public int getLogin() {
		return login;
	}
	public void setLogin(int login) {
		this.login = login;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	
	
}

package springbook.user.domain;

//old
/*public enum Level{
	BASIC(1), SILVER(2), GOLD(3);
	
	private final int value;
	
	Level(int value){
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}
	
	public static Level valueOf(int value) {
		switch (value) {
			case 1: return BASIC;
			case 2: return SILVER;
			case 3: return GOLD;
			default:throw new AssertionError("Unknown value : " + value);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Level.valueOf(1));
		System.out.println(Level.valueOf(2));
		System.out.println(Level.valueOf(3));
	}
}*/
//new
public enum Level{
	
	//이늄 선언에 DB에 저장할 값과 함께 다음 단계의 레벨 정보도 추가한다.
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);
	
	private final int value;
	private final Level next;
	
	Level(int value, Level next){
		this.value = value;
		this.next = next;
	}
	
	public int intValue() {
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	
	public static Level valueOf(int value) {
		switch (value) {
			case 1: return BASIC;
			case 2: return SILVER;
			case 3: return GOLD;
			default:throw new AssertionError("Unknown value : " + value);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Level.valueOf(1));
		System.out.println(Level.valueOf(2));
		System.out.println(Level.valueOf(3));
	}
}
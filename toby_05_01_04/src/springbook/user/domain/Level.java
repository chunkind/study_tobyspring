package springbook.user.domain;

public enum Level{
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
}
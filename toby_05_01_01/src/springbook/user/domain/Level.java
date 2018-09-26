package springbook.user.domain;

//new : �н��׽�Ʈ1.
//public enum Level {
//	BASIC(), SILVER(), GOLD();
//	// enum �н� �׽�Ʈ.
//	public static void main(String[] args) {
//		Level e1 = Level.BASIC;
//		Level e2 = Level.SILVER;
//		Level e3 = Level.GOLD;
//		System.out.println(Level.BASIC);
//		System.out.println(Level.SILVER);
//		System.out.println(Level.GOLD);
//	}
//}
//new : �̴� �߰�.
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
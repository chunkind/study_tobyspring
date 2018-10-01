package springbook.learningtest.spring.factorybean;

//new : 스프링의 팩토리 빈을 학습하는 테스트.
public class Message {
	String text;
	
	//생성자가 private이라 외부에서 생성 불가능.
	private Message(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public static Message newMessage(String text) {
		return new Message(text);
	}

}

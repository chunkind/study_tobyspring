package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

//new
public class DynamicProxyTest {

	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Chun"), is("Hello Chun"));
		assertThat(hello.sayHi("Chun"), is("Hi Chun"));
		assertThat(hello.sayThankYou("Chun"), is("Thank You Chun"));
		
		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
		
	}
	
}

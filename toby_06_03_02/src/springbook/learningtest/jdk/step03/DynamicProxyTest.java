package springbook.learningtest.jdk.step03;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class DynamicProxyTest {

	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Chun"), is("Hello Chun"));
		assertThat(hello.sayHi("Chun"), is("Hi Chun"));
		assertThat(hello.sayThankYou("Chun"), is("Thank You Chun"));
		
		//old
//		Hello proxiedHello = new HelloUppercase(new HelloTarget());
//		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
//		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
//		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
		//new : 다이나믹 프록시 적용.
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader() , 
				new Class[] {Hello.class} , 
				new UppercaseHandler(new HelloTarget()));
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
		
	}
	
}
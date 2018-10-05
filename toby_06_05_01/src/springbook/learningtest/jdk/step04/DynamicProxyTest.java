package springbook.learningtest.jdk.step04;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

public class DynamicProxyTest {

	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Chun"), is("Hello Chun"));
		assertThat(hello.sayHi("Chun"), is("Hi Chun"));
		assertThat(hello.sayThankYou("Chun"), is("Thank You Chun"));
		
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader() , 
				new Class[] {Hello.class} , 
				new UppercaseHandler(new HelloTarget()));
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
		
	}
	
	//new 
	@Test
	public void proxyFactoryBean(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget()); //타깃 설정
		pfBean.addAdvice(new UppercaseAdvice()); //부가기능을 담은 어드바이스를 추가 한다. 여러 개를 추가할 수도 있다.
		
		//FactoryBean 이므로 getObject()로 생성된 프록시를 가져온다.
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
	}
	
}
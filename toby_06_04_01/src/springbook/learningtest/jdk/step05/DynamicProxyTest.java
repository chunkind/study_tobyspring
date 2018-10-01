package springbook.learningtest.jdk.step05;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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
	
	//new 
	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		//메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성.
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		//이름 비교조건 설정. sayH로 묶어서 한 번에 추가 
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		//메소드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가 기능(대문자변환)이 적용되지 않는다.
		assertThat(proxiedHello.sayThankYou("Chun"), is("Thank You Chun"));
	}
	
}


















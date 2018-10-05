package springbook.learningtest.jdk.step06;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
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
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("THANK YOU CHUN"));
	}
	
	@Test
	public void pointcutAdvisor(){
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
		assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
		assertThat(proxiedHello.sayThankYou("Chun"), is("Thank You Chun"));
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		//포인트컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*");
		
		//테스트
		//적용클래스
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		//적용클래스 아닐때
		class HelloWorld extends HelloTarget{};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		//적용클래스
		class HelloToby extends HelloTarget{};
		checkAdviced(new HelloToby(), classMethodPointcut, true);
	}

	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced/*적용여부*/) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced) {
			assertThat(proxiedHello.sayHello("Chun"), is("HELLO CHUN"));
			assertThat(proxiedHello.sayHi("Chun"), is("HI CHUN"));
			assertThat(proxiedHello.sayThankYou("Chun"), is("Thank You Chun"));
		} else {
			assertThat(proxiedHello.sayHello("Chun"), is("Hello Chun"));
			assertThat(proxiedHello.sayHi("Chun"), is("Hi Chun"));
			assertThat(proxiedHello.sayThankYou("Chun"), is("Thank You Chun"));
		}
	}
	
}


















package springbook.learningtest.spring.ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import springbook.learningtest.spring.ioc.resource.Hello;
import springbook.learningtest.spring.ioc.resource.StringPrinter;

public class ApplicationContextTest {

	/**
	 * StaticApplicationContext 테스트 코드 
	 */
	@Test
	public void registerBean() {
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.registerSingleton("hello1", Hello.class);
		
		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		ac.registerBeanDefinition("hello2", helloDef);
		
		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));
		
		assertThat(hello1, is(not(hello2)));
		
		assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
	}
	
	/**
	 * StaticApplicationContext 테스트 코드  
	 */
	@Test
	public void registerBeanWithDependency() {
		StaticApplicationContext ac = new StaticApplicationContext();
		
		ac.registerBeanDefinition("printer", 
			new RootBeanDefinition(StringPrinter.class));
		
		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		helloDef.getPropertyValues().addPropertyValue("printer", 
			new RuntimeBeanReference("printer"));
		
		ac.registerBeanDefinition("hello", helloDef);

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	
	@Test
	public void genericApplicationContext() {
		GenericApplicationContext ac = new GenericApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		reader.loadBeanDefinitions(
			"springbook/learningtest/spring/ioc/genericApplication");
		
		//모든 메타정보가 등록이 완료됐으니 애플리케이션 컨테이너를 초기화하라는 명령이다.
		ac.refresh();
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		//검증 내용은 StaticApplicationContext 테스트와 동일하다.
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
}







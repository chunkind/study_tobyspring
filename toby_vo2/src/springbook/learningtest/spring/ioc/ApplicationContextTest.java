package springbook.learningtest.spring.ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import springbook.learningtest.spring.ioc.bean.AnnotatedHello;
import springbook.learningtest.spring.ioc.bean.AnnotatedHelloConfig;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.resource.Hello;
import springbook.learningtest.spring.ioc.resource.StringPrinter;

public class ApplicationContextTest {
	
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	/**
	 * StaticApplicationContext 
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
	 * StaticApplicationContext  
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
		//XmlBeanDefinitionReader는 기본적으로 클래스패스로 정의된 리소스로부터 파일을 읽는다.
		reader.loadBeanDefinitions(
			"springbook/learningtest/spring/ioc/genericApplication");
		
		//모든 메타정보가 등록이 완료됐으니 애플리케이션 컨테이너를 초기화하라는 명령이다.
		ac.refresh();
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		//검증 내용은 StaticApplicationContext 테스트와 동일하다.
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	@Test
	public void genericXmlApplicationContext() {
		GenericApplicationContext ac = new GenericXmlApplicationContext(
			basePath + "genericApplicationContext.xml");
		
		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();
		
		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}
	
	@Test
	public void contextHierachy() {
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");

		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		child.refresh();
		
		Printer printer = child.getBean("printer", Printer.class);
		assertThat(printer, is(notNullValue()));
		
		Hello hello = child.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));
		
		hello.print();
		assertThat(printer.toString(), is("Hello Child"));
	}
	
	@Test
	public void simpleBeanScanning() {
		//@Component가 붙은 클래스를 스캔할 패키지를 넣어서 컨텍스트를 만들어준다.
		//생성과 동시에 자동으로 스캔과 등록이 진행된다.
		ApplicationContext ctx = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));
	}
	
	
	@Test
	public void configurationBean() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
		
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));

		AnnotatedHelloConfig config = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertThat(config, is(notNullValue()));
		
		assertThat(config.annotatedHello(), is(sameInstance(hello)));
		assertThat(config.annotatedHello(), is(config.annotatedHello()));
		
		System.out.println(ctx.getBean("systemProperties").getClass());
	}
	
}







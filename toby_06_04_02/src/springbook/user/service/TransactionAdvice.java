package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

//new : TransactionHandler -> TransactionAdvice
public class TransactionAdvice implements MethodInterceptor{
	
	private PlatformTransactionManager transactionManager;
	
	//old : 삭제
//	private Object target;
//	private String pattern;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	//old : 삭제 
//	public void setPattern(String pattern) {
//		this.pattern = pattern;
//	}
//	public void setTarget(Object target) {
//		this.target = target;
//	}
	

	//old
//	@Override
//	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//		if(method.getName().startsWith(pattern)) {
//			return invokeInTransaction(method, args);
//		} else {
//			return method.invoke(target, args);
//		}
//	}
//	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
//		TransactionStatus status =
//				this.transactionManager.getTransaction(new DefaultTransactionDefinition());
//		try {
//			Object ret = method.invoke(target, args);
//			this.transactionManager.commit(status);
//			return ret;
//		} catch (InvocationTargetException e) {
//			this.transactionManager.rollback(status);
//			throw e.getTargetException();
//		}
//	}
	//new
	/*
	 * 타깃을 호출하는 기능을 가진 콜백 오브젝트를 프록시로부터 받는다.
	 * 덕분에 어드바이스는 특정 타깃에 의존하지 않고 재사용 가능하다.
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		TransactionStatus status =
				this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			/*
			 * 콜백을 호출해서 타깃의 메소드를 실행한다.
			 * 타깃 메소드 호출 전후로 필요한 부가기능을 넣을 수 있다.
			 * 경우에 따라서 타깃이 아예 호출되지 않게 하거나 재시도를 위한 반복적인
			 * 호출도 가능하다.
			 */
			Object ret = invocation.proceed();
			
			this.transactionManager.commit(status);
			return ret;
			
		//old
//		} catch (InvocationTargetException e) {
		//new : JDK 다이내믹 프록시가 제공하는 Method와는 달리 스프링의 Methodinvocation을 통한 타깃
		//호출은 예외가 포장되지 않고 타깃에서 보낸 그대로 전달된다.
		} catch (RuntimeException e) {
			
			this.transactionManager.rollback(status);
			
			//old
//			throw e.getTargetException();
			//new
			throw e;
		}
	}


}














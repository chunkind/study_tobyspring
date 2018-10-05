package springbook.learningtest.jdk.step05;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

//new
public class UppercaseAdvice implements MethodInterceptor{

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		/*
		 * 리플랙션의 Method와 달리 메소드 실행 시 타깃 오브젝트를
		 * 전달할 필요가 없다. MethodInvocation은 메소드 정보와
		 * 함께 타깃 오브젝트를 알고 있기 때문이다.
		 */
		String ret = (String)invocation.proceed();
		return ret.toUpperCase(); //부가기능 적용
	}

}

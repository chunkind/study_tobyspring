package springbook.user.service;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

//new
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut{

	@Override
	public void setMappedName(String mappedClassName) {
		this.setClassFilter(new SimpleClassFilter(mappedClassName));
	}
	
	static class SimpleClassFilter implements ClassFilter{

		String mappedName;
		
		public SimpleClassFilter(String mappedName) {
			this.mappedName = mappedName;
		}

		@Override
		public boolean matches(Class<?> clazz) {
			return PatternMatchUtils.simpleMatch(mappedName
				, clazz.getSimpleName());
		}
		
	}
	
}

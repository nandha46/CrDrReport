/**
 * 
 */
package in.trident.crdr.advice;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspectj advice class to provide centralized logging to all methods
 * 
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9d
 * 
 * @since 08 mar 2022
 *
 */
@Component
@Aspect
public class LoggingAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAdvice.class);

	@Pointcut(value = "execution(* in.trident.crdr.*.*.*(..) )")
	public void myPointcut() {
		// provides pointcut to LoggingAspect
	}

	@Around("myPointcut()")
	public Object appLogger(ProceedingJoinPoint pjp) {
		String methodName = pjp.getSignature().getName();
		String className = pjp.getTarget().getClass().toString();
		Object[] array = pjp.getArgs();
		Object obj = null;
		String args = null;
		String errorName = "Error caught from " + methodName;
		try {
			args = Arrays.toString(array);

			LOGGER.info("METHOD_ENTRY: \"{}\", \"{}\", Args: {} ", methodName, className, args);
			obj = pjp.proceed();
			args = obj == null ? "null" : obj.toString();
		} catch (Throwable e) {
			LOGGER.error(errorName, e);
		}
		LOGGER.info("METHOD_EXIT: \"{}\", \"{}\", Args: {} ", methodName, className, args);
		return obj;

	}
}

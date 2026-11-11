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
 * @author Nandhakumar Subramanian
 * @version 0.0.9e
 * @since 08 mar 2022
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
	public Object appLogger(ProceedingJoinPoint pjp) throws Throwable {
		String methodName = pjp.getSignature().getName();
		String className = pjp.getTarget() != null ? pjp.getTarget().getClass().getName() : "unknown";
		LOGGER.debug("METHOD_ENTRY: \"{}\", \"{}\", Args: {}", methodName, className, Arrays.toString(pjp.getArgs()));
		try {
			Object result = pjp.proceed();
			LOGGER.debug("METHOD_EXIT: \"{}\", \"{}\", Result: {}", methodName, className, result);
			return result;
		} catch (Throwable e) {
			LOGGER.error("Error caught from {}.{}", className, methodName, e);
			throw e;
		}
	}
}

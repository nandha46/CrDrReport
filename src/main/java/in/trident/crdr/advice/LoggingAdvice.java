/**
 * 
 */
package in.trident.crdr.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		ObjectMapper mapper = new ObjectMapper();
		Object[] array = pjp.getArgs();
		Object obj = null;
		String args = null;
		try {
		 args = mapper.writeValueAsString(array);

		LOGGER.info(" Method Entry: {}, ClassName: {}, Args: {} ", methodName, className, args);
		 obj = pjp.proceed();
			args = mapper.writeValueAsString(obj);
		} catch (Throwable e) {
			LOGGER.error("Error caught from Method",e);
		} 
		LOGGER.info(" Method Exit: {}, ClassName: {}, Args: {} ", methodName, className, args);
		return obj;

	}
}

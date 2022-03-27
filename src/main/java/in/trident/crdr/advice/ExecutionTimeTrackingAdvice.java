/**
 * 
 */
package in.trident.crdr.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.stereotype.Component;


/**
 * @author nandh
 *
 */
@Component
@Aspect
public class ExecutionTimeTrackingAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeTrackingAdvice.class);

	@Around("@annotation(in.trident.crdr.annotations.TrackExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
		Profiler profiler = new Profiler(pjp.getTarget().getClass().toString());
		profiler.setLogger(LOGGER);
		profiler.start(pjp.getSignature().getName());
		Object obj =  pjp.proceed();
		String elapsedTime = profiler.stop().toString();
		LOGGER.info("\n {}", elapsedTime);
		return obj;
	}

}

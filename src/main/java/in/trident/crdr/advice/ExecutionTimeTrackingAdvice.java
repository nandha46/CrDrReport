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

/**
 * @author nandh
 *
 */
@Aspect
public class ExecutionTimeTrackingAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeTrackingAdvice.class);

	@Around("@annotation(in.trident.crdr.annotations.ExcecutionTimeTracker)")
	public void logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
		Profiler profiler = new Profiler(pjp.getTarget().getClass().toString());
		profiler.setLogger(LOGGER);
		profiler.start(pjp.getSignature().getName());
		pjp.proceed();
		String elapsedTime = profiler.stop().toString();
		LOGGER.info("{}", elapsedTime);
	}

}

/**
 * 
 */
package in.trident.crdr.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Annotaion to track method execution time
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9d
 * 
 * @since 8 mar 2022
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcecutionTimeTracker {

}

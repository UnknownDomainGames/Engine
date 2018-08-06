package unknowndomain.engine.event;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Listener {

	Order order() default Order.DEFAULT;

	boolean receiveCancelled() default false;
}

<<<<<<< HEAD:EngineAPI/src/main/java/unknowndomain/engine/event/Subscribe.java
package unknowndomain.engine.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Subscribe {
	
	EventPriority priority() default EventPriority.NORMAL;

	boolean receiveCancelled() default false;
}
=======
package unknowndomain.engine.api.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Subscribe {
	
	Order order() default Order.DEFAULT;

	boolean receiveCancelled() default false;
}
>>>>>>> remotes/origin/dev:EngineAPI/src/main/java/unknowndomain/engine/api/event/Subscribe.java

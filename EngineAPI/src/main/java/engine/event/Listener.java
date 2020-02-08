package engine.event;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for annotation event handle method.
 * <p>
 * Event handle method should be a <b>public member method without return value</b>.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Listener {

    /**
     * A order of event handler for deciding when it will be call.
     */
    Order order() default Order.DEFAULT;

    /**
     * @return True if you want to receive cancelled event, false if not.
     */
    boolean receiveCancelled() default false;
}

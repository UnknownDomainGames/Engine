package unknowndomain.engine.mod;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Mark mod main class.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Mod {

    /**
     * Mod identified name.
     */
    String value();
}

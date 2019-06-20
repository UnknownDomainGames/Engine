package unknowndomain.engine.mod.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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

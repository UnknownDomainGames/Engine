package nullengine.annotation;

import java.lang.annotation.*;

/**
 * Annotate any experimental code library.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD,
        ElementType.FIELD, ElementType.PACKAGE, ElementType.MODULE})
@Documented
public @interface Experimental {
}

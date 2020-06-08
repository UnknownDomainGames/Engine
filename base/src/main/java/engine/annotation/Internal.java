package engine.annotation;

import java.lang.annotation.*;

/**
 * Annotate any internal code.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD,
        ElementType.FIELD, ElementType.PACKAGE, ElementType.MODULE})
@Documented
public @interface Internal {
}

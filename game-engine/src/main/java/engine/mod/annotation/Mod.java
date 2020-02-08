package engine.mod.annotation;

import engine.mod.InstallationType;

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
    String id();

    String version() default "1.0.0";

    String name() default "";

    InstallationType installationType() default InstallationType.CLIENT_REQUIRED;

    String description() default "";

    String license() default "";

    String url() default "";

    String logo() default "";

    String[] authors() default {};

    String[] permissions() default {};

    Dependency[] dependencies() default {};

    CustomElement[] customElements() default {};

    boolean generateMetadata() default true;
}

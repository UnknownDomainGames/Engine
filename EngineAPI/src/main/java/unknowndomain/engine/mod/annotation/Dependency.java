package unknowndomain.engine.mod.annotation;

import unknowndomain.engine.mod.DependencyType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Dependency {

    String id();

    String version() default "*";

    DependencyType type() default DependencyType.REQUIRED;
}

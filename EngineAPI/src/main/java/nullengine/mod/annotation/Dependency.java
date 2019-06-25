package nullengine.mod.annotation;

import nullengine.mod.DependencyType;

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

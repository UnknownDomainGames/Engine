package nullengine.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto listen static listener when annotated class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoListen {

    Bus bus() default Bus.ENGINE;

    boolean clientOnly() default false;

    enum Bus {
        ENGINE, MOD
    }
}

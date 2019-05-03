package unknowndomain.engine.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoListen {

    EventBus value() default EventBus.ENGINE;

    enum EventBus {
        ENGINE, MOD
    }
}

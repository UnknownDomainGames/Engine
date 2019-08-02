package nullengine.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A annotation for injecting registered object to annotated static field.
 *
 * @see nullengine.event.mod.ModRegistrationEvent.Post
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegObject {

    /**
     * @return The unique name of registered object which is wanted to inject.
     */
    String value();
}

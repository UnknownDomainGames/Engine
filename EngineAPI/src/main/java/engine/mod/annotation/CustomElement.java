package engine.mod.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface CustomElement {

    String key();

    /**
     * @return json element
     */
    String value();
}

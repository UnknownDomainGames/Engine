package unknowndomain.engine.client.gui.component;

/*
* shadow :a component change with another object.
*/

import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Shadow extends Control {
    public Shadow(){
        super();
    }
    public <T> void setReflectObjectTo(@NonNull SimpleMutableObjectValue<T> t, @NonNull ReflectObjectToShadow<T> reflect) {
        t.addChangeListener((ob, o, n) -> reflect.accept(n, this));
    }
    public interface ReflectObjectToShadow<T> {
        void accept(T t, Shadow slider);
    }
}

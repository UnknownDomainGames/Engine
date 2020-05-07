package engine.gui.shape;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import engine.graphics.shape.Path2D;

public abstract class PathElement {

    private MutableBooleanValue absolute;

    public abstract void addPath(Path2D path);

    public final MutableBooleanValue absolute() {
        if (absolute == null) {
            absolute = new SimpleMutableBooleanValue(true);
        }
        return absolute;
    }

    public final boolean isAbsolute() {
        return absolute == null || absolute.get();
    }

    public final void setAbsolute(boolean absolute) {
        absolute().set(absolute);
    }
}

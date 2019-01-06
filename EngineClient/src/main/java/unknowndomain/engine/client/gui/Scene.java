package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.value.MutableIntValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableIntValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableIntValue;

import java.util.Objects;

public class Scene {

    private final MutableIntValue width = new SimpleMutableIntValue();
    private final MutableIntValue height = new SimpleMutableIntValue();

    private Container root;

    public Scene(Container root) {
        setRoot(root);
    }

    public ObservableIntValue width() {
        return width.toImmutable();
    }

    public int getWidth() {
        return width.get();
    }

    public ObservableIntValue height() {
        return height.toImmutable();
    }

    public int getHeight() {
        return height.get();
    }

    public void setSize(int width, int height) {
        this.width.set(width);
        this.height.set(height);
        updateRoot();
    }

    public Container getRoot() {
        return root;
    }

    public void setRoot(Container root) {
        this.root = Objects.requireNonNull(root);
        updateRoot();
    }

    private void updateRoot() {
        this.root.width.set(getWidth() - root.x().get());
        this.root.height.set(getHeight() - root.y().get());
        this.root.needsLayout();
    }

    public void update() {
        root.layout();
    }
}

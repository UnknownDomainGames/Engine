package engine.client.hud;

import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.control.Control;

public abstract class HUDControl extends Control {

    private final String name;

    private MutableObjectValue<Node> content;

    public HUDControl(String name) {
        this.name = name;
        setVisible(false);
        visible().addChangeListener((observable, oldValue, newValue) -> onVisibleChanged(newValue));
    }

    public String getName() {
        return name;
    }

    public MutableObjectValue<Node> content() {
        if (content == null) {
            content = new SimpleMutableObjectValue<>();
            content.addChangeListener((observable, oldValue, newValue) -> {
                ObservableList<Node> children = getChildren();
                children.remove(oldValue);
                children.add(newValue);
                needsLayout();
            });
        }
        return content;
    }

    public Node getContent() {
        return content == null ? null : content.get();
    }

    public void setContent(Node content) {
        content().set(content);
    }

    public void onVisibleChanged(boolean visible) {
    }

    @Override
    public float computeWidth() {
        Node content = getContent();
        return content == null ? 0 : content.prefWidth();
    }

    @Override
    public float computeHeight() {
        Node content = getContent();
        return content == null ? 0 : content.prefHeight();
    }

    @Override
    protected void layoutChildren() {
        Node content = getContent();
        if (content != null) {
            layoutInArea(content, 0, 0, width().get(), height().get());
        }
    }
}

package engine.client.hud;

import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.control.Control;
import engine.registry.Name;
import engine.registry.Registrable;

public abstract class HUDControl extends Control implements Registrable<HUDControl> {

    private Name name;
    private MutableObjectValue<Node> content;

    public HUDControl() {
        setVisible(false);
        visible().addChangeListener((observable, oldValue, newValue) -> onVisibleChanged(newValue));
    }

    @Override
    public Class<HUDControl> getEntryType() {
        return HUDControl.class;
    }

    @Override
    public HUDControl name(String name) {
        if (this.name != null) throw new Error("Duplicated register " + name);
        this.name = Name.fromString(name);
        return this;
    }

    @Override
    public HUDControl name(Name name) {
        if (this.name != null) throw new Error("Duplicated register " + name);
        this.name = name;
        return this;
    }

    @Override
    public Name getName() {
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
    public double computeWidth() {
        Node content = getContent();
        return content == null ? 0 : content.prefWidth();
    }

    @Override
    public double computeHeight() {
        Node content = getContent();
        return content == null ? 0 : content.prefHeight();
    }

    @Override
    protected void layoutChildren() {
        Node content = getContent();
        if (content != null) {
            layoutInArea(content, 0, 0, getWidth(), getHeight());
        }
    }
}

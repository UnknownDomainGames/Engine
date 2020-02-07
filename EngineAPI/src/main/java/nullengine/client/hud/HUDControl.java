package nullengine.client.hud;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Node;
import nullengine.client.gui.control.Control;

public abstract class HUDControl extends Control {

    private final String name;

    private MutableObjectValue<Node> content;

    public HUDControl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MutableObjectValue<Node> content() {
        if (content == null) {
            content = new SimpleMutableObjectValue<>();
            content.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        }
        return content;
    }

    public Node getContent() {
        return content == null ? null : content.get();
    }

    public void setContent(Node content) {
        content().set(content);
    }
}

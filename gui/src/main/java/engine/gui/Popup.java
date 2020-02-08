package engine.gui;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.event.EventHandler;
import engine.gui.event.EventType;
import engine.gui.rendering.ComponentRenderer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Popup extends Parent{

    private MutableObjectValue<Node> content = new SimpleMutableObjectValue<>();

    private List<Pair<EventType, EventHandler>> insertedHandlers = new ArrayList<>();

    public MutableObjectValue<Node> content() {
        return content;
    }

    public Popup(Node content){
        this.content.addChangeListener((observable, oldValue, newValue) -> {
            if(oldValue != null) {
                getChildren().remove(oldValue);
            }
            getChildren().add(newValue);
        });
        this.content.set(content);
    }

    @Override
    public float prefWidth() {
        return content.isPresent() ? content.get().prefWidth() : 0;
    }

    @Override
    public float prefHeight() {
        return content.isPresent() ? content.get().prefHeight() : 0;
    }

    public List<Pair<EventType, EventHandler>> getInsertedHandlers() {
        return insertedHandlers;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return (ComponentRenderer<Popup>) (component, graphics) -> {
            graphics.pushClipRect(component.x().get(), component.y().get(), component.prefWidth(), component.prefHeight());
            component.content().ifPresent(node -> node.getRenderer().render(node, graphics));
            graphics.popClipRect();
        };
    }
}

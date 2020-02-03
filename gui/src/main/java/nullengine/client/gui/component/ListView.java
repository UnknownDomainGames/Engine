package nullengine.client.gui.component;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Node;
import nullengine.client.gui.Parent;
import nullengine.client.gui.layout.ScrollPane;
import nullengine.client.gui.layout.VBox;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListView<T> extends Control {
    private final ScrollPane scrollPane;

    private Parent parent;

    public static final Supplier<? extends Parent> DEFAULT_CONTAINER_FACTORY = VBox::new;

    private final Function<T, Node> defaultContentFactory = (obj) -> new Label(obj.toString());
    private MutableObjectValue<Supplier<? extends Parent>> containerFactory = new SimpleMutableObjectValue<>(DEFAULT_CONTAINER_FACTORY);
    private MutableObjectValue<Function<T, ? extends Node>> contentFactory = new SimpleMutableObjectValue<>(defaultContentFactory);

    private ObservableList<T> items = ObservableCollections.observableList(new ArrayList<>());

    public ListView() {
        scrollPane = new ScrollPane();
        getChildren().add(scrollPane);
        this.getSize().prefWidth().bindBidirectional(scrollPane.getSize().prefWidth());
        this.getSize().prefHeight().bindBidirectional(scrollPane.getSize().prefHeight());
        items.addChangeListener(change -> update());
        containerFactory.addChangeListener((observable, oldValue, newValue) -> {
            parent = null;
            update();
        });
//        padding().addChangeListener((observable, oldValue, newValue) -> layoutInArea(scrollPane));
        border().addChangeListener((observable, oldValue, newValue) -> padding().setValue(newValue.getInsets()));
    }

    private void update() {
        if (parent == null) {
            parent = containerFactory.getValue().get();
            scrollPane.setContent(parent);
        }
        parent.getChildren().clear();
        for (T item : items) {
            var c = contentFactory.getValue().apply(item);
            parent.getChildren().add(c);
        }
    }

    @Override
    public float computeWidth() {
        return scrollPane.computeWidth();
    }

    @Override
    public float computeHeight() {
        return scrollPane.computeHeight();
    }

    public ObservableList<T> items() {
        return items;
    }
}

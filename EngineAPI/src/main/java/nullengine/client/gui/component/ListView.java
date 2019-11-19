package nullengine.client.gui.component;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Component;
import nullengine.client.gui.Container;
import nullengine.client.gui.layout.ScrollPane;
import nullengine.client.gui.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListView<T> extends Control {
    private final ScrollPane scrollPane;

    private Container container;

    public static final Supplier<? extends Container> DEFAULT_CONTAINER_FACTORY = VBox::new;

    private final Function<T, Component> defaultContentFactory = (obj) -> new Label(obj.toString());
    private MutableObjectValue<Supplier<? extends Container>> containerFactory = new SimpleMutableObjectValue<>(DEFAULT_CONTAINER_FACTORY);
    private MutableObjectValue<Function<T, ? extends Component>> contentFactory = new SimpleMutableObjectValue<>(defaultContentFactory);

    private ObservableList<T> items = ObservableCollections.observableList(new ArrayList<>());

    public ListView(){
        scrollPane = new ScrollPane();
        getChildren().add(scrollPane);
        this.getSize().prefWidth().bindBidirectional(scrollPane.getSize().prefWidth());
        this.getSize().prefHeight().bindBidirectional(scrollPane.getSize().prefHeight());
        items.addChangeListener(change -> update());
        containerFactory.addChangeListener((observable, oldValue, newValue) -> {
            container = null;
            update();
        });
//        padding().addChangeListener((observable, oldValue, newValue) -> layoutInArea(scrollPane));
        border().addChangeListener((observable, oldValue, newValue) -> padding().setValue(newValue.getInsets()));
    }

    private void update(){
        if(container == null){
            container = containerFactory.getValue().get();
            scrollPane.setContent(container);
        }
        container.getChildren().clear();
        for (T item : items) {
            var c = contentFactory.getValue().apply(item);
            container.getChildren().add(c);
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

    //restoring Container's definition
    @Override
    public List<Component> getPointingComponents(float posX, float posY) {
        var list = new ArrayList<Component>();
        for (Component component : getChildren()) {
            if (component.contains(posX, posY)) {
                if (component instanceof Container) {
                    var container = (Container) component;
                    if (!(component instanceof Control)) {
                        list.add(container);
                    }
                    list.addAll(container.getPointingComponents(posX - container.x().get(), posY - container.y().get()));

                } else {
                    list.add(component);
                }
            }
        }
        return list;
    }
}

package nullengine.client.gui.control;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Node;
import nullengine.client.gui.layout.HBox;
import nullengine.client.gui.layout.Pane;
import nullengine.client.gui.layout.ScrollPane;
import nullengine.client.gui.layout.VBox;
import nullengine.client.gui.misc.Orientation;
import nullengine.client.gui.text.Text;

import java.util.ArrayList;
import java.util.function.Function;

public class ListView<T> extends Control {

    private MutableObjectValue<Orientation> orientation;

    private MutableObjectValue<Function<T, ? extends Node>> cellFactory = new SimpleMutableObjectValue<>((value) -> new Text(value.toString()));

    private ObservableList<T> items = ObservableCollections.observableList(new ArrayList<>());

    private ScrollPane scrollPane;
    private Pane contentPane;

    public ListView() {
        scrollPane = new ScrollPane();
        getChildren().add(scrollPane);
        this.getSize().prefWidth().bindBidirectional(scrollPane.getSize().prefWidth());
        this.getSize().prefHeight().bindBidirectional(scrollPane.getSize().prefHeight());
        items.addChangeListener(change -> {
            // TODO: waiting for optimization
            var contentChildren = contentPane.getChildren();
            var cellFactory = this.cellFactory.get();
            contentChildren.clear();
            for (T item : items) contentChildren.add(cellFactory.apply(item));
        });
//        padding().addChangeListener((observable, oldValue, newValue) -> layoutInArea(scrollPane));
        border().addChangeListener((observable, oldValue, newValue) -> padding().setValue(newValue.getInsets()));
        updateContentPane();
    }

    public final MutableObjectValue<Orientation> orientation() {
        if (orientation == null) {
            orientation = new SimpleMutableObjectValue<>(Orientation.VERTICAL);
            orientation.addChangeListener((observable, oldValue, newValue) -> updateContentPane());
        }
        return orientation;
    }

    public final Orientation getOrientation() {
        return orientation == null ? Orientation.VERTICAL : orientation.get();
    }

    public final void setOrientation(Orientation orientation) {
        orientation().set(orientation);
    }

    private void updateContentPane() {
        Pane newContentPane;
        if (getOrientation() == Orientation.HORIZONTAL) {
            newContentPane = new HBox();
        } else {
            newContentPane = new VBox();
        }
        if (contentPane != null) {
            newContentPane.getChildren().addAll(contentPane.getChildren());
        }
        contentPane = newContentPane;
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

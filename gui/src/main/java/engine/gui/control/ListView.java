package engine.gui.control;

import com.github.mouse0w0.observable.collection.ListChangeListener;
import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.layout.HBox;
import engine.gui.layout.Pane;
import engine.gui.layout.VBox;
import engine.gui.misc.Orientation;
import engine.gui.misc.Size;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ListView<T> extends Control {

    private MutableObjectValue<Orientation> orientation;

    @SuppressWarnings("Convert2Diamond")
    // T in ListCell<T> inside lambda has to be explicitly stated in order to compile successfully
    private MutableObjectValue<Function<ListView<T>, ListCell<T>>> cellFactory = new SimpleMutableObjectValue<>((listView) -> new ListCell<T>() {
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
                setText(null);
            } else if (item instanceof Node) {
                setText(null);
                var oldNode = getGraphic();
                var newNode = (Node) item;
                if (oldNode == null || !oldNode.equals(newNode)) {
                    setGraphic(newNode);
                }
            } else {
                setText(item == null ? "null" : item.toString());
                setGraphic(null);
            }
        }
    });

    private MutableObjectValue<ObservableList<T>> items = new SimpleMutableObjectValue<>(ObservableCollections.observableList(new ArrayList<>()));

    private MutableObjectValue<SelectionModel<T>> selectionModel = new SimpleMutableObjectValue<>(new SingleSelectionModel<>(items()));

    private MutableObjectValue<FocusModel<T>> focusModel = new SimpleMutableObjectValue<>(new ListViewFocusModel<>(this));

    private ScrollPane scrollPane;
    private Pane contentPane;

    public ListView() {
        scrollPane = new ScrollPane();
        getChildren().add(scrollPane);
        this.getSize().prefWidth().bindBidirectional(scrollPane.getSize().prefWidth());
        this.getSize().prefHeight().bindBidirectional(scrollPane.getSize().prefHeight());
        items.get().addChangeListener(change -> {
            needsLayout();
        });
//        padding().addChangeListener((observable, oldValue, newValue) -> layoutInArea(scrollPane));
        border().addChangeListener((observable, oldValue, newValue) -> setPadding(newValue.getInsets()));
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
            newContentPane.getSize().setPrefHeight(Size.USE_PARENT_VALUE);
        } else {
            newContentPane = new VBox();
            newContentPane.getSize().setPrefWidth(Size.USE_PARENT_VALUE);
        }
        if (contentPane != null) {
            newContentPane.getChildren().addAll(contentPane.getChildren());
        }
        contentPane = newContentPane;
        scrollPane.setContent(contentPane);
    }

    @Override
    public float computeWidth() {
        return scrollPane.computeWidth();
    }

    @Override
    public float computeHeight() {
        return scrollPane.computeHeight();
    }

    public MutableObjectValue<ObservableList<T>> observableItems() {
        return items;
    }

    public ObservableList<T> items() {
        return items.get();
    }

    public MutableObjectValue<SelectionModel<T>> selectionModel() {
        return selectionModel;
    }

    private LinkedList<ListCell<T>> cells = new LinkedList<>();
    private LinkedList<ListCell<T>> pile = new LinkedList<>();

    protected void dumpToPile() {
        var size = cells.size();
        for (int i = 0; i < size; i++) {
            addToPile(cells.removeFirst());
        }
    }

    protected void addToPile(ListCell<T> cell) {
        Objects.requireNonNull(cell);

        pile.addLast(cell);
    }

    @Nonnull
    private ListCell<T> getAvailableCell(int prefIndex) {
        ListCell<T> cell = null;

        var size = pile.size();
        for (int i = 0; i < size; i++) {
            var _cell = pile.get(i);
            if (_cell == null) continue;

            if (_cell.index().get() == prefIndex) {
                cell = _cell;
                pile.remove(i);
                break;
            }
        }

        if (cell == null) {
            if (!pile.isEmpty()) {
                //TODO: if we need even-odd styling, pick one from pile
                cell = pile.removeFirst();
            } else {
                var cellFactory = this.cellFactory.get();
                cell = cellFactory.apply(this);
            }
        }

        return cell;
    }

    int lastCellCount = -1;

    @Override
    protected void layoutChildren() {
        // TODO: waiting for optimization
        var contentChildren = contentPane.getChildren();
        contentChildren.clear();
        boolean needRecreateCells = false; //TODO: field
        if (needRecreateCells) {
            for (var cell : cells) {
                cell.index().set(-1);
                cell.owner().set(null);
            }
            cells.clear();
        }
        boolean layoutCellsOnly = false;
        if (layoutCellsOnly) {
            for (ListCell<T> cell : cells) {
                cell.needsLayout();
            }
            layoutCellsOnly = false;
        } else {

            var shouldRebuild = cells.isEmpty() || lastCellCount != observableItems().optional().map(ObservableList::size).orElse(0);
            if (shouldRebuild) {
                //TODO: we might store old cells into a pile, and then retrieve it back here if suitable
                dumpToPile();
                ObservableList<T> get = items.get();
                for (int i = 0; i < get.size(); i++) {
                    var listCell = getAvailableCell(i);
                    listCell.owner().set(this);
                    listCell.index().set(i);
                    cells.addLast(listCell);
                }
                lastCellCount = observableItems().optional().map(List::size).orElse(-1);
            }
        }
        contentChildren.addAll(cells);
        super.layoutChildren();
    }

    static class ListViewFocusModel<T> extends FocusModel<T> {

        private final ListView<T> listView;

        private int itemCount;

        private final ListChangeListener<T> listChangeListener = change -> {
            updateItemCount();
        };

        public ListViewFocusModel(ListView<T> listView) {
            if (listView == null) {
                throw new IllegalArgumentException("listView");
            }
            this.listView = listView;
            listView.observableItems().addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null) oldValue.removeChangeListener(listChangeListener);
                if (newValue != null) newValue.addChangeListener(listChangeListener);
                updateItemCount();
            });
            if (listView.items() != null) {
                listView.items().addChangeListener(listChangeListener);
            }
        }

        @Override
        protected int getItemCount() {
            return itemCount;
        }

        private void updateItemCount() {
            if (listView == null) itemCount = -1;
            else {
                itemCount = listView.items() != null ? -1 : listView.items().size();
            }
        }

        @Override
        protected T getItem(int index) {
            if (itemCount == -1) return null;
            if (index < 0 || itemCount >= index) return null;
            return listView.items().get(index);
        }
    }
}

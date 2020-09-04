package engine.gui.control;

import com.github.mouse0w0.observable.collection.ListChangeListener;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.*;
import engine.gui.misc.Background;
import engine.util.Color;

import java.util.Objects;

public class ListCell<T> extends Labeled {

    private MutableObjectValue<ListView<T>> owner = new SimpleMutableObjectValue<>();

    private MutableObjectValue<T> item = new SimpleMutableObjectValue<>();

    private MutableIntValue index = new SimpleMutableIntValue(-1);

    private MutableBooleanValue markedEmpty = new SimpleMutableBooleanValue();

    private MutableBooleanValue selected = new SimpleMutableBooleanValue();

    protected void updateItem(T item, boolean empty) {
        this.item.set(item);
        this.markedEmpty.set(empty);
        if (empty && selected.get()) {
            setSelected(false);
        }
    }

    protected boolean isItemChanged(T oldItem, T newItem) {
        return !Objects.equals(oldItem, newItem);
    }

    public void setSelected(boolean flag) {
        if (flag && markedEmpty.get()) return;
        selected.set(flag);
    }

    public ObservableBooleanValue selected() {
        return selected.toUnmodifiable();
    }

    public MutableBooleanValue markedEmpty() {
        return markedEmpty;
    }

    public MutableIntValue index() {
        return index;
    }

    public MutableObjectValue<T> item() {
        return item;
    }

    public MutableObjectValue<ListView<T>> owner() {
        return owner;
    }

    private final ListChangeListener<T> itemChangeListener = c -> {
        var index = this.index.get();
        var items = owner.isPresent() ? owner.get().items() : null;
        var itemCount = items == null ? 0 : items.size();

        var indexAfterChangeFromIndex = index >= c.getFrom();
        var indexBeforeChangeToIndex = index < c.getTo() || index == itemCount;
        var indexInRange = indexAfterChangeFromIndex && indexBeforeChangeToIndex;

        boolean shouldUpdate = indexInRange || (indexAfterChangeFromIndex && (c.wasRemoved() || c.wasAdded()));

        if (shouldUpdate) {
            updateItem(-1);
        }
    };
    private final ValueChangeListener<Integer> selectedIndexChangeListener = (observable1, oldValue1, newValue1) -> {
        updateSelection();
    };
    private final ValueChangeListener<ObservableList<T>> itemListChangeListener = (observable1, oldValue1, newValue1) -> {
        if (oldValue1 != null) {
            oldValue1.removeChangeListener(itemChangeListener);
        }
        if (newValue1 != null) {
            newValue1.addChangeListener(itemChangeListener);
        }
        updateItem(-1);
    };
    private final ValueChangeListener<SelectionModel<T>> selectionModelChangeListener = (observable1, oldValue1, newValue1) -> {
        if (oldValue1 != null) {
            oldValue1.selectedIndex().removeChangeListener(selectedIndexChangeListener);
        }
        if (newValue1 != null) {
            newValue1.selectedIndex().addChangeListener(selectedIndexChangeListener);
        }
        updateSelection();
    };

    public ListCell() {
        setText(null);
        owner.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.observableItems().ifPresent(list -> list.removeChangeListener(itemChangeListener));
                oldValue.selectionModel().ifPresent(model -> model.selectedIndex().removeChangeListener(selectedIndexChangeListener));
                oldValue.observableItems().removeChangeListener(itemListChangeListener);
                oldValue.selectionModel().removeChangeListener(selectionModelChangeListener);
            }
            if (newValue != null) {
                newValue.observableItems().ifPresent(list -> list.addChangeListener(itemChangeListener));
                newValue.selectionModel().ifPresent(model -> model.selectedIndex().addChangeListener(selectedIndexChangeListener));
                newValue.observableItems().addChangeListener(itemListChangeListener);
                newValue.selectionModel().addChangeListener(selectionModelChangeListener);
            }
            updateItem(-1);
            updateSelection();
            needsLayout();
        });
        index.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                updateItem(oldValue);
                updateSelection();
            }
        });
        //TODO: stylize
        setOnMouseEntered(event -> {
            if (!selected().get())
                if (parent().isPresent() ? contains(parent().get().relativePos(event.getScreenX(), event.getScreenY())) : contains(event.getX(), event.getY())) {
                    background().set(Background.fromColor(Color.BLUE));
                }
        });

        setOnMouseExited(event -> {
            if (!selected().get()) {
                background().set(Background.NOTHING);
            }
        });

        selected().addChangeListener((observable, oldValue, newValue) -> {
            if (newValue) {
                background().set(Background.fromColor(Color.fromRGB(0x007fff)));
            }
            if (!newValue) {
                background().set(Background.NOTHING);
            }
        });

        setOnMouseClicked(event -> {
            if (owner.isPresent()) {
                if (owner.get().selectionModel().isPresent()) {
                    owner.get().selectionModel().get().select(index.get());
                }
            }
        });
    }

    @Override
    public float computeWidth() {
        if (parent().isPresent()) {
            return parent().get().getWidth();
        }
        return super.computeWidth();
    }

    private boolean initializedEmpty;

    private void updateItem(int oldIndex) {
        var index = index().get();
        var items = owner.isPresent() ? owner.get().items() : null;
        var size = items != null ? items.size() : 0;

        var valid = items != null && 0 <= index && index < size;
        var oldItem = item.get();
        var empty = markedEmpty.get();
        if (valid) {
            var newItem = items.get(index);
            if (oldIndex != index || isItemChanged(oldItem, newItem)) {
                updateItem(newItem, false);
            }
        } else {
            if ((!empty && oldItem != null) || !initializedEmpty) {
                updateItem(null, true);
                initializedEmpty = true;
            }
        }
    }

    private void updateSelection() {
        if (markedEmpty.get()) return;
        var index = index().get();
        if (index == -1 || owner.isEmpty()) return;

        var model = owner.get().selectionModel().get();
        if (model == null) {
            setSelected(false);
        } else {
            boolean isSelected = model.isSelected(index);
            if (selected.get() != isSelected) {
                setSelected(isSelected);
            }
        }

    }

}

package engine.gui.control;

import com.github.mouse0w0.observable.collection.ObservableList;

public class SingleSelectionModel<T> extends SelectionModel<T> {

    private final ObservableList<T> list;

    public SingleSelectionModel(ObservableList<T> observableList) {
        this.list = observableList;
        selectedIndex().addChangeListener((observable, oldValue, newValue) -> {
            selectedItem().set(getModelItem(selectedIndex().get()));
        });
    }

    protected T getModelItem(int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    protected int getModelSize() {
        return list.size();
    }

    @Override
    public void select(int i) {
        if (getModelSize() == 0 || i < 0 || i >= getModelSize())
            return;
        selectedIndex().set(i);
    }

    @Override
    public void select(T obj) {
        if (obj == null) {
            selectedIndex().set(-1);
            selectedItem().set(null);
            return;
        } else {
            for (int i = 0; i < getModelSize(); i++) {
                var item = getModelItem(i);
                if (item != null && item.equals(obj)) {
                    select(i);
                    return;
                }
            }
        }

        selectedItem().set(obj);
    }

    @Override
    public void unselect(int i) {
        if (selectedIndex().get() == i) {
            unselectAll();
        }
    }

    @Override
    public void unselectAll() {
        selectedIndex().set(-1);
    }

    @Override
    public boolean isSelected(int i) {
        return selectedIndex().get() == i;
    }

    @Override
    public boolean isNothingSelected() {
        return list.isEmpty() || selectedIndex().get() == -1;
    }

    @Override
    public void selectPreviousObject() {
        if (selectedIndex().get() == 0) return;
        select(selectedIndex().get() - 1);
    }

    @Override
    public void selectNextObject() {
        select(selectedIndex().get() + 1);
    }
}

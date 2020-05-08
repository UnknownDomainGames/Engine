package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableIntValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableIntValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;

public abstract class SelectionModel<T> {
    private final MutableIntValue selectedIndex = new SimpleMutableIntValue(-1);
    private final MutableObjectValue<T> selectedItem = new SimpleMutableObjectValue<>();

    public MutableIntValue selectedIndex() {
        return selectedIndex;
    }

    public MutableObjectValue<T> selectedItem() {
        return selectedItem;
    }

    public abstract void select(int i);

    public abstract void select(T obj);

    public abstract void unselect(int i);

    public abstract void unselectAll();

    public abstract boolean isSelected(int i);

    public abstract boolean isNothingSelected();

    public abstract void selectPreviousObject();

    public abstract void selectNextObject();
}

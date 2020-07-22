package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableIntValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableIntValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;

public abstract class FocusModel<T> {
    private final MutableIntValue focusedIndex = new SimpleMutableIntValue(-1);

    private final MutableObjectValue<T> focusedItem = new SimpleMutableObjectValue<>();

    public MutableIntValue focusedIndex() {
        return focusedIndex;
    }

    public MutableObjectValue<T> focusedItem() {
        return focusedItem;
    }

    protected abstract int getItemCount();

    protected abstract T getItem(int index);

    public boolean isFocused(int index) {
        return 0 <= index && index < getItemCount() && focusedIndex.get() == index;
    }

    public void focus(int index) {
        if (index < 0 || index >= getItemCount()) {
            focusedIndex().set(-1);
        } else {
            focusedIndex().set(index);
        }
    }

    public void focusPrevious() {
        if (focusedIndex().get() == -1) {
            focus(0);
        } else if (focusedIndex().get() > 0) {
            focus(focusedIndex().get() - 1);
        }
    }

    public void focusNext() {
        if (focusedIndex().get() == -1) {
            focus(0);
        } else if (focusedIndex().get() < getItemCount() - 1) {
            focus(focusedIndex().get() + 1);
        }
    }
}

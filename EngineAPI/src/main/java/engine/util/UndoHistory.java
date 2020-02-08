package engine.util;

import java.util.ArrayDeque;

public class UndoHistory<E> {
    private ArrayDeque<E> history;
    private ArrayDeque<E> future;
    private int maximum;

    public UndoHistory(int maximum) {
        this.maximum = maximum;
        history = new ArrayDeque<>(maximum != -1 ? maximum : 10);
        future = new ArrayDeque<>(maximum != -1 ? maximum : 10);
    }

    public void pushHistory(E ele) {
        if (maximum != -1 && history.size() >= maximum) {
            history.removeLast();
        }
        future.clear();
        history.push(ele);
    }

    public E undo() {
        if (undoable()) {
            E a = history.pop();
            future.push(a);
            return a;
        }
        return null;
    }

    public boolean undoable() {
        return history.size() > 0;
    }

    public E redo() {
        if (redoable()) {
            E a = future.pop();
            history.push(a);
            return a;
        }
        return null;
    }

    public boolean redoable() {
        return future.size() > 0;
    }

}

package engine.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ListenerList implements Iterable<ListenerWrapper> {
    private final List<ListenerList> children = new ArrayList<>();
    private final List<ListenerWrapper> listeners = new ArrayList<>();

    public void register(ListenerWrapper listener) {
        addListener(listener);
        for (ListenerList child : children) {
            child.addListener(listener);
        }
    }

    public void unregister(ListenerWrapper listener) {
        removeListener(listener);
        for (ListenerList child : children) {
            child.removeListener(listener);
        }
    }

    private void addListener(ListenerWrapper listener) {
        int left = 0, right = listeners.size();
        while (left < right) {
            int mid = (left + right) >>> 1;
            if (compareListener(listener, listeners.get(mid)) < 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        listeners.add(left, listener);
    }

    private void removeListener(ListenerWrapper listener) {
        listeners.remove(listener);
    }

    private int compareListener(ListenerWrapper o1, ListenerWrapper o2) {
        return o1.getOrder().compareTo(o2.getOrder());
    }

    public void addParent(ListenerList parent) {
        parent.children.add(this);
        for (ListenerWrapper listener : parent.listeners) {
            addListener(listener);
        }
    }

    public void addChild(ListenerList child) {
        children.add(child);
        for (ListenerWrapper listener : listeners) {
            child.addListener(listener);
        }
    }

    @Override
    public Iterator<ListenerWrapper> iterator() {
        return listeners.iterator();
    }
}

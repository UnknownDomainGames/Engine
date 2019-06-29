package nullengine.event.misc;

import nullengine.util.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ListenerList {

    private final Class<?> eventType;
    private final List<ListenerList> children = new ArrayList<>();

    private final Collection<RegisteredListener> listeners = SortedList.wrap(new ArrayList<>(), Comparator.comparingInt(o -> o.getOrder().ordinal()));

    public ListenerList(Class<?> eventType) {
        this.eventType = eventType;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void register(RegisteredListener listener) {
        listeners.add(listener);
        children.forEach(listenerList -> listenerList.listeners.add(listener));
    }

    public void unregister(RegisteredListener listener) {
        listeners.remove(listener);
        children.forEach(listenerList -> listenerList.listeners.remove(listener));
    }

    public void addParent(ListenerList parent) {
        parent.children.add(this);
        listeners.addAll(parent.listeners);
    }

    public void addChild(ListenerList child) {
        children.add(child);
        child.listeners.addAll(listeners);
    }

    public Collection<RegisteredListener> getListeners() {
        return listeners;
    }
}

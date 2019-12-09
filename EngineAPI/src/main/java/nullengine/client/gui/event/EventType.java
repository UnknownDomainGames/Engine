package nullengine.client.gui.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventType<T extends Event> {

    public static final EventType<Event> ROOT = new EventType<>();

    private final String name;
    private final EventType<? super T> superType;

    private List<EventType<?>> childTypes;

    private EventType() {
        this.name = "Event";
        this.superType = null;
    }

    public EventType(String name) {
        this(name, ROOT);
    }

    public EventType(String name, EventType<? super T> superType) {
        this.name = name;
        this.superType = Objects.requireNonNull(superType, "SuperType cannot be null.");
        this.superType.addChildType(this);
    }

    public EventType<? super T> getSuperType() {
        return superType;
    }

    public String getName() {
        return name;
    }

    private void addChildType(EventType<? extends T> childType) {
        if (childTypes == null) childTypes = new ArrayList<>();
        childTypes.add(childType);
    }

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }
}

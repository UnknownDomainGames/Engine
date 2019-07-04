package nullengine.event;

import nullengine.component.Component;
import nullengine.component.GameObject;

import java.util.Set;
import java.util.function.Predicate;

public class ComponentFilter implements Predicate<GameObject> {
    private Set<Class<? extends Component>> validation;

    private ComponentFilter(Set<Class<? extends Component>> validation) {
        this.validation = validation;
    }

    public Predicate<GameObject> of(Set<Class<? extends Component>> validation) {
        return new ComponentFilter(validation);
    }

    @Override
    public boolean test(GameObject gameObject) {
        Set<Class<?>> components = gameObject.getComponents();
        for (Class<? extends Component> aClass : validation) {
            if (!components.contains(aClass)) {
                return false;
            }
        }
        return true;
    }
}

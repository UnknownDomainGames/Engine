package nullengine.item.component;

import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.item.Item;

public interface HitEntityBehavior extends Component {
    void onStart(Entity hitter, Item item, Entity entity);

    boolean onKeep(Entity hitter, Item item, Entity entity, int tickElapsed);

    void onStart(Entity hitter, Item item, Entity entity, int tickElapsed);
}

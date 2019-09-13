package nullengine.item.component;

import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.item.Item;
import nullengine.player.Player;

public interface ClickEntityBehavior extends Component {
    void onStart(Player player, Item item, Entity entity);

    boolean onKeep(Player player, Item item, Entity entity, int tickElapsed);

    void onStart(Player player, Item item, Entity entity, int tickElapsed);
}

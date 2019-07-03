package nullengine.event.world.block.cause;

import nullengine.entity.Entity;
import nullengine.event.cause.Cause;
import nullengine.item.Item;
import nullengine.world.World;

public interface BlockChangeCause extends Cause {
    interface EntityTrigger extends BlockChangeCause {
        Entity getEntity();
    }

    interface WorldTrigger extends BlockChangeCause {
        World getWorld(); // TODO: we don't have terrain gen API yet; replace to terrain api later?
    }

    interface WithItem extends BlockChangeCause {
        Item getItem();
    }
}

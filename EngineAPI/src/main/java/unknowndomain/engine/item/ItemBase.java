package unknowndomain.engine.item;

import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemBase extends RegistryEntry.Impl<Item> implements Item {
    private Map<Class<? extends Component>,Component> components = new HashMap<>();

    protected ItemBase(){
        setComponent(ItemPrototype.HitBlockBehavior.class, new ItemPrototype.HitBlockBehavior() {
            @Override
            public void onHit(Player player, ItemStack itemStack, RayTraceBlockHit hit) {
                //TODO: use send packet
                if(hit.isSuccess()) {
                    player.getWorld().removeBlock(hit.getPos(), null);
                }
            }

            @Override
            public boolean onKeep(Player player, ItemStack itemStack, RayTraceBlockHit hit, int tickElapsed) {
                return false;
            }

            @Override
            public void onHitStop(Player player, ItemStack itemStack, RayTraceBlockHit hit) {
                //TODO: send packet
            }
        });
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return (Optional<T>) Optional.ofNullable(components.get(type));
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.containsKey(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, T value) {
        components.put(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.remove(type);
    }

}

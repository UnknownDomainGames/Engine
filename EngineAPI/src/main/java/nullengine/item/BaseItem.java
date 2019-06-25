package nullengine.item;

import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import nullengine.player.Player;
import nullengine.registry.RegistryEntry;
import nullengine.world.collision.RayTraceBlockHit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class BaseItem extends RegistryEntry.Impl<Item> implements Item {
    private final ComponentContainer components = new ComponentContainer();

    protected BaseItem() {
        setComponent(ItemPrototype.HitBlockBehavior.class, new ItemPrototype.HitBlockBehavior() {
            @Override
            public void onHit(Player player, ItemStack itemStack, RayTraceBlockHit hit) {
                //TODO: use send packet
                if (hit.isSuccess()) {
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
        return components.getComponent(type);
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        components.setComponent(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.removeComponent(type);
    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }

}

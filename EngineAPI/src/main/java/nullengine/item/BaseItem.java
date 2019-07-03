package nullengine.item;

import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import nullengine.entity.Entity;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Event;
import nullengine.event.world.block.BlockDestroyEvent;
import nullengine.event.world.entity.EntityHitEvent;
import nullengine.event.world.item.ItemBreakEvent;
import nullengine.event.world.item.ItemThrowAwayEvent;
import nullengine.item.component.*;
import nullengine.player.Player;
import nullengine.registry.RegistryEntry;
import nullengine.world.World;
import nullengine.world.collision.RayTraceBlockHit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
public class BaseItem extends RegistryEntry.Impl<Item> implements Item {
    private final ComponentContainer components = new ComponentContainer();
    protected BaseItem() {
        setComponent(HitBlockBehavior.class, new HitBlockBehavior() {
            @Override
            public void onHit(Player player, ItemStack itemStack, RayTraceBlockHit hit) {
                //TODO: use send packet
                if (hit.isSuccess()) {
                    player.getWorld().removeBlock(hit.getPos(), null);
                    new BlockDestroyEvent(player.getWorld(),hit.getPos(),hit.getBlock(),null);
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
        setComponent(HitEntityBehavior.class,new HitEntityBehavior(){
            @Override
            public void onStart(Entity hitter, Item item, Entity entity, int tickElapsed) {
                Event event=new EntityHitEvent(entity.getWorld(),hitter,entity,(BaseItem)item);
            }

            @Override
            public void onStart(Entity hitter, Item item, Entity entity) {
                Event event=new EntityHitEvent(entity.getWorld(),hitter,entity,(BaseItem)item);
            }

            @Override
            public boolean onKeep(Entity hitter, Item item, Entity entity, int tickElapsed) {
                return false;
            }
        });
        setComponent(UseBehavior.class, ((player, itemStack) -> {
            Event itemDamageEvent=new ItemBreakEvent(itemStack.getItem(),player.getWorld(),player);
        }));
        setComponent(ItemDurabilityComponent.class,new ItemDurabilityComponent(){
            @Override
            public int getDurability(int durability) {
                return -1;
            }

            @Override
            public void setDurability(int durability) {

            }
        });
        setComponent(ItemHarmComponent.class, new ItemHarmComponent() {
            @Override
            public void setHarm(int harm) {

            }

            @Override
            public int getHarm() {
                return 0;
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

    public void onThrowAway(BaseItem item,World world,Entity entity, ItemEntity itemEntity){
        Event itemThrowEvent=new ItemThrowAwayEvent(item,world,entity,itemEntity);
    }
}

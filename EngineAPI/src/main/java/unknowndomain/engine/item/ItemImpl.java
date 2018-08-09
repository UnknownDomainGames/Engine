package unknowndomain.engine.item;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class ItemImpl extends RegistryEntry.Impl<Item> implements Item {
    private ItemPrototype.UseBlockBehavior useBlockBehavior;
    private ItemPrototype.HitBlockBehavior hitBlockBehavior;
    private ItemPrototype.UseBehavior useBehavior;

    public ItemImpl(ItemPrototype.UseBlockBehavior useBlockBehavior, ItemPrototype.HitBlockBehavior hitBlockBehavior, ItemPrototype.UseBehavior useBehavior) {
        this.useBlockBehavior = useBlockBehavior;
        this.hitBlockBehavior = hitBlockBehavior;
        this.useBehavior = useBehavior;
    }

    @Override
    public void onUseStart(World world, Player player, Item item) {
        useBehavior.onUseStart(world, player, item);
    }

    @Override
    public boolean onUsing(World world, Player player, Item item, int tickElapsed) {
        return useBehavior.onUsing(world, player, item, tickElapsed);
    }

    @Override
    public void onUseStop(World world, Player player, Item item, int tickElapsed) {
        useBehavior.onUseStop(world, player, item, tickElapsed);
    }

    @Override
    public void onHit(Player player, Item item, Block.Hit hit) {
        hitBlockBehavior.onHit(player, item, hit);
    }


    @Override
    public void onUseBlockStart(World world, Player player, Item item, Block.Hit hit) {
        useBlockBehavior.onUseBlockStart(world, player, item, hit);
    }

    @Override
    public boolean onUsingBlock(Player player, Item item, Block.Hit hit, int tickElapsed) {
        return useBlockBehavior.onUsingBlock(player, item, hit, tickElapsed);
    }

    @Override
    public void onUseBlockStop(Player player, Item item, Block.Hit hit, int tickElapsed) {
        useBlockBehavior.onUseBlockStop(player, item, hit, tickElapsed);
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }


}

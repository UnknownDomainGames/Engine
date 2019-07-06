package nullengine.item;

import nullengine.block.Block;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.item.component.UseBlockBehavior;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockItem extends BaseItem {

    private final Block block;

    public BlockItem(@Nonnull Block block) {
        this.block = Objects.requireNonNull(block);
        name(block.getName());
        initComponent();
    }

    protected void initComponent() {
        setComponent(UseBlockBehavior.class,
                (player, itemStack, hit) -> hit.ifSuccess($ -> $.getWorld().setBlock($.getPos().offset($.getFace()), block, new BlockChangeCause.EntityCause(player.getControlledEntity()))));
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }


}

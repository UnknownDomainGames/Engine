package nullengine.item;

import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.item.component.ActivateBlockBehavior;

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
        setComponent(ActivateBlockBehavior.class, (itemStack, hit, cause) -> {
            if (cause instanceof BlockInteractCause.PlayerCause) {
                hit.ifSuccess($ -> hit.getWorld().setBlock(hit.getPos().offset(hit.getDirection()), block,
                        new BlockChangeCause.PlayerCause(((BlockInteractCause.PlayerCause) cause).getPlayer())));
            }
        });
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }


}

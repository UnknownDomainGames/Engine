package nullengine.item;

import nullengine.block.Block;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockItem extends BaseItem {

    private final Block block;

    public BlockItem(@Nonnull Block block) {
        this.block = Objects.requireNonNull(block);
        registerName(block.getRegisterName());
        initComponent();
    }

    protected void initComponent() {
        setComponent(ItemPrototype.UseBlockBehavior.class,
                (player, itemStack, hit) -> hit.ifSuccess($ -> $.getWorld().setBlock($.getPos().offset($.getFace()), block, null)));
    }

    @Nonnull
    public Block getBlock() {
        return block;
    }


}

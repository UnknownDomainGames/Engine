package unknowndomain.engine.item;

import unknowndomain.engine.block.Block;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemBlock extends ItemBase {

    private final Block block;

    public ItemBlock(@Nonnull Block block) {
        this.block = Objects.requireNonNull(block);
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

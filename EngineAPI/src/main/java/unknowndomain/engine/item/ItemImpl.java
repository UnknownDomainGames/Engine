package unknowndomain.engine.item;

class ItemImpl extends ItemBase implements Item {
    private ItemPrototype.UseBlockBehavior useBlockBehavior;
    private ItemPrototype.HitBlockBehavior hitBlockBehavior;
    private ItemPrototype.UseBehavior useBehavior;

    ItemImpl(ItemPrototype.UseBlockBehavior useBlockBehavior, ItemPrototype.HitBlockBehavior hitBlockBehavior, ItemPrototype.UseBehavior useBehavior) {
        this.useBlockBehavior = useBlockBehavior;
        this.hitBlockBehavior = hitBlockBehavior;
        this.useBehavior = useBehavior;
    }
}

package unknowndomain.engine.item;

import org.apache.commons.lang3.Validate;

public class ItemBuilder {
    private ItemPrototype.UseBlockBehavior useBlockBehavior;
    private ItemPrototype.HitBlockBehavior hitBlockBehavior;
    private ItemPrototype.UseBehavior useBehavior;

    private String id;

    private ItemBuilder(String id) {
        this.id = id;
    }

    public static ItemBuilder create(String id) {
        Validate.notNull(id);
        return new ItemBuilder(id);
    }

    public ItemBuilder setUseBlockBehavior(ItemPrototype.UseBlockBehavior useBlockBehavior) {
        Validate.notNull(useBlockBehavior);
        this.useBlockBehavior = useBlockBehavior;
        return this;
    }

    public ItemBuilder setUseBehavior(ItemPrototype.UseBehavior useBehavior) {
        Validate.notNull(useBehavior);
        this.useBehavior = useBehavior;
        return this;
    }


    public ItemBuilder setHitBlockBehavior(ItemPrototype.HitBlockBehavior hitBlockBehavior) {
        Validate.notNull(hitBlockBehavior);
        this.hitBlockBehavior = hitBlockBehavior;
        return this;
    }

    public Item build() {
        return new ItemImpl(useBlockBehavior, hitBlockBehavior, useBehavior).setRegistryName(id);
    }
}
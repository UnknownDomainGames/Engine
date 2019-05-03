package unknowndomain.engine.block;

import org.apache.commons.lang3.Validate;
import org.joml.AABBd;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BlockBuilder {

    private List<AABBd> aabBds = new ArrayList<>();
    private String path;
    private boolean noCollision = false;

    private BlockPrototype.PlaceBehavior placeBehavior = BlockPrototype.DEFAULT_PLACE;
    private BlockPrototype.ActivateBehavior activateBehavior = BlockPrototype.DEFAULT_ACTIVATE;
    private BlockPrototype.ClickBehavior clickBehavior = BlockPrototype.DEFAULT_CLICK;
    private BlockPrototype.DestroyBehavior destroyBehavior = BlockPrototype.DEFAULT_DESTROY;

    private BlockBuilder(String path) {
        this.path = path;
    }

    public BlockBuilder setNoCollision() {
        this.noCollision = true;
        return this;
    }

    public static BlockBuilder create(String path) {
        Validate.notNull(path);
        return new BlockBuilder(path);
    }

    public BlockBuilder addBoundingBox(AABBd boundingBox) {
        aabBds.add(boundingBox);
        return this;
    }

    public BlockBuilder setPlaceBehavior(BlockPrototype.PlaceBehavior placeBehavior) {
        this.placeBehavior = placeBehavior;
        return this;
    }

    public BlockBuilder setActivateBehavior(BlockPrototype.ActivateBehavior activateBehavior) {
        this.activateBehavior = activateBehavior;
        return this;
    }

    public BlockBuilder setClickBehavior(BlockPrototype.ClickBehavior clickBehavior) {
        this.clickBehavior = clickBehavior;
        return this;
    }

    public BlockBuilder setDestroyBehavior(BlockPrototype.DestroyBehavior destroyBehavior) {
        this.destroyBehavior = destroyBehavior;
        return this;
    }

    public Block build() {
        AABBd[] boxes = noCollision ? new AABBd[0] : aabBds.size() == 0 ? new AABBd[]{Block.DEFAULT_BOUNDING_BOX} : aabBds.toArray(new AABBd[aabBds.size()]);
        return new BlockShared(boxes, placeBehavior, activateBehavior, clickBehavior, destroyBehavior)
                .registerName(path);
    }
}
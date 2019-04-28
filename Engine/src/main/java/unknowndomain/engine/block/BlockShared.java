package unknowndomain.engine.block;

import org.joml.AABBd;

@Deprecated
class BlockShared extends BlockBase {

    BlockShared(AABBd[] boundingBox, BlockPrototype.PlaceBehavior placeBehavior, BlockPrototype.ActivateBehavior activateBehavior, BlockPrototype.ClickBehavior clickBehavior, BlockPrototype.DestroyBehavior destroyBehavior) {
        setBoundingBoxes(boundingBox);

        setComponent(BlockPrototype.PlaceBehavior.class, placeBehavior);
        setComponent(BlockPrototype.ActivateBehavior.class, activateBehavior);
        setComponent(BlockPrototype.ClickBehavior.class, clickBehavior);
        setComponent(BlockPrototype.DestroyBehavior.class, destroyBehavior);
    }
}

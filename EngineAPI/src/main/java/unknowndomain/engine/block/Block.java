package unknowndomain.engine.block;

import org.joml.AABBd;
import unknowndomain.engine.component.RuntimeObject;
import unknowndomain.engine.registry.RegistryEntry;

public interface Block extends RegistryEntry<Block>, RuntimeObject, BlockPrototype.PlaceBehavior, BlockPrototype.ActiveBehavior, BlockPrototype.DestroyBehavior, BlockPrototype.TouchBehavior {
    // think about blockstate and tileentity...

    AABBd DEFAULT_BOUNDING_BOX = new AABBd(0, 0, 0, 1, 1, 1);

    AABBd[] getBoundingBoxes();
}

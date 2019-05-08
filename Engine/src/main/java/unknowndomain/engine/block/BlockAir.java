package unknowndomain.engine.block;

import org.joml.AABBd;

public class BlockAir extends BlockBase {

    public BlockAir() {
        setBoundingBoxes(new AABBd[0]);
        registerName("air");
    }
}

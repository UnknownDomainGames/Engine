package unknowndomain.engine.block.impl;

import org.joml.AABBd;
import unknowndomain.engine.block.BlockBase;

public class BlockAir extends BlockBase {

    public BlockAir() {
        setBoundingBoxes(new AABBd[0]);
        localName("air");
    }
}

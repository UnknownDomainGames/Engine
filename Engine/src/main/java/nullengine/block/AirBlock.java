package nullengine.block;

import org.joml.AABBd;

public class AirBlock extends BaseBlock {

    public AirBlock() {
        setBoundingBoxes(new AABBd[0]);
        registerName("air");
    }
}

package unknowndomain.engine.unclassified;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.world.World;

import java.util.List;

public class BlockStone extends Block {
    private List<BlockObject> all;

    public BlockStone() {
        all = new BlockObjectBuilder()
                .setPath(new ResourcePath("ud", "stone"))
                .build();
    }

    @Override
    public BlockObject createObject(World context) {
        return all.get(0);
    }

    @Override
    public List<BlockObject> getAllStates() {
        return all;
    }
}

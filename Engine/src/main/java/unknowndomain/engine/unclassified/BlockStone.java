package unknowndomain.engine.unclassified;

import unknowndomain.engine.api.unclassified.Block;
import unknowndomain.engine.api.unclassified.BlockObject;
import unknowndomain.engine.api.unclassified.World;
import unknowndomain.engine.api.util.DomainedPath;

import java.util.List;

public class BlockStone extends Block {
    private List<BlockObject> all;

    public BlockStone() {
        all = new BlockObjectBuilder()
                .setPath(new DomainedPath("ud", "stone"))
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

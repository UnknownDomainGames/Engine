package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.util.Facing;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BlockModelBaker {

    public BakedBlockModel bake(BlockModel model) {
        List<float[]> meshes = new ArrayList<>(7);
        for(Facing facing : Facing.values()) {

        }
        return new BakedBlockModel(meshes);
    }

    public void bakeQuad(BlockModelQuad modelQuad, ByteBuffer buffer) {
    }
}

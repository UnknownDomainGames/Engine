package nullengine.client.rendering.model;

import nullengine.client.rendering.model.block.data.BlockModel;

public interface ModelBaker {

    boolean isAccepts(BlockModel data);

    BakedModel bake(BlockModel data);

}

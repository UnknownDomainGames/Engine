package nullengine.client.rendering.model.item;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.model.ModelBaker;
import nullengine.client.rendering.model.data.ModelData;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.Direction;

public final class ItemGenerationModelBaker implements ModelBaker {
    @Override
    public boolean isAccepts(ModelData data) {
        return AssetURL.fromString(data.url, data.parent).getLocation().equals("item/generate");
    }

    @Override
    public BakedModel bake(ModelData data) {
        return null;
    }

    public final class Model implements BakedModel {

        @Override
        public void putVertexes(GLBuffer buffer, int coveredFace) {

        }

        @Override
        public boolean isFullFace(Direction direction) {
            return false;
        }
    }
}

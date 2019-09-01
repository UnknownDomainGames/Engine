package nullengine.client.rendering.model;

import nullengine.client.rendering.model.data.ModelData;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.Direction;

public final class DefaultModelBaker implements ModelBaker {
    @Override
    public boolean isAccepts(ModelData data) {
        return true;
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

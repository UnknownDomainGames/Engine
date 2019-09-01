package nullengine.client.rendering.model;

import nullengine.client.rendering.model.data.ModelData;

public interface ModelBaker {

    boolean isAccepts(ModelData data);

    BakedModel bake(ModelData data);

}

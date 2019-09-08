package nullengine.client.rendering.model;

import nullengine.client.asset.AssetURL;

import java.util.List;

public interface Model {

    BakedModel bake();

    List<AssetURL> getTextures();
}

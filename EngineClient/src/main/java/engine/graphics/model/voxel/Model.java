package engine.graphics.model.voxel;

import engine.client.asset.AssetURL;
import engine.graphics.model.BakedModel;
import engine.graphics.texture.TextureAtlasRegion;

import java.util.Collection;
import java.util.function.Function;

public interface Model {

    BakedModel bake(Function<AssetURL, TextureAtlasRegion> textureGetter);

    Collection<AssetURL> getTextures();
}

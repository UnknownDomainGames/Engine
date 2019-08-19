package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;

public interface TextureAtlas extends Texture {

    TextureAtlasPart addTexture(AssetURL url);
}

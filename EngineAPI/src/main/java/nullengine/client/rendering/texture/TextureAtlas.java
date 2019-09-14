package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;

public interface TextureAtlas extends Texture {

    TextureAtlasPart getTexture(AssetURL url);

    TextureAtlasPart addTexture(AssetURL url);

    void reload();
}

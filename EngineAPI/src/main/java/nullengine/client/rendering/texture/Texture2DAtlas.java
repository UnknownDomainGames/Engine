package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;

public interface Texture2DAtlas extends Texture2D {

    TextureAtlasPart getTexture(AssetURL url);

    TextureAtlasPart addTexture(AssetURL url);

    void reload();
}

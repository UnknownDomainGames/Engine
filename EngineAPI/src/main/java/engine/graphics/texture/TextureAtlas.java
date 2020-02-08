package engine.graphics.texture;

import engine.client.asset.AssetURL;

public interface TextureAtlas extends Texture2D {

    TextureAtlasRegion getTexture(AssetURL url);

    TextureAtlasRegion addTexture(AssetURL url);

    void reload();
}

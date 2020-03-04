package engine.graphics.texture;

import engine.client.asset.AssetURL;

public interface TextureAtlas {

    TextureAtlasRegion getTexture(AssetURL url);

    TextureAtlasRegion addTexture(AssetURL url);

    Texture2D getTexture();

    void reload();
}

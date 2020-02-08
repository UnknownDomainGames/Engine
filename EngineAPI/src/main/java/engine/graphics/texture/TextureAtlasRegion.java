package engine.graphics.texture;

import engine.client.asset.AssetURL;
import engine.graphics.image.BufferedImage;

public interface TextureAtlasRegion {

    AssetURL getUrl();

    BufferedImage getData();

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();
}

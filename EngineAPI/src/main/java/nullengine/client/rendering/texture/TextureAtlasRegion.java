package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.image.BufferedImage;

public interface TextureAtlasRegion {

    AssetURL getUrl();

    BufferedImage getData();

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();
}

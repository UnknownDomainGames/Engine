package engine.graphics.texture;

import engine.client.asset.AssetURL;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlasImpl implements TextureAtlas {

    private final Map<AssetURL, TextureAtlasRegionImpl> textures = new HashMap<>();

    private Texture2D texture;

    @Override
    public TextureAtlasRegion getTexture(AssetURL url) {
        return textures.get(url);
    }

    @Override
    public TextureAtlasRegion addTexture(AssetURL url) {
        return textures.computeIfAbsent(url, key -> new TextureAtlasRegionImpl(url));
    }

    @Override
    public Texture2D getTexture() {
        return texture;
    }

    @Override
    public void reload() {
        var textureMap = new TextureAtlasBuilder();
        for (var part : textures.values()) {
            part.reload();
            part.setUv(textureMap.add(part.getData()));
        }

        textureMap.finish();
        if (texture != null) {
            texture.dispose();
        }
        texture = Texture2D.builder().build(textureMap.getResult());
        textureMap.dispose();
    }

    public void cleanCache() {
        textures.values().forEach(TextureAtlasRegionImpl::cleanCache);
    }
}

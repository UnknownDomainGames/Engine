package engine.graphics.texture;

import engine.client.asset.AssetURL;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlasImpl implements TextureAtlas {

    private final Map<AssetURL, TextureAtlasRegionImpl> textures = new HashMap<>();

    private Texture2D bakedTextureAtlas;

    @Override
    public TextureAtlasRegion getTexture(AssetURL url) {
        return textures.get(url);
    }

    @Override
    public TextureAtlasRegion addTexture(AssetURL url) {
        return textures.computeIfAbsent(url, key -> new TextureAtlasRegionImpl(url));
    }

    @Override
    public void reload() {
        var textureMap = new TextureAtlasBuilder();
        for (var part : textures.values()) {
            part.reload();
            part.setUv(textureMap.add(part.getData()));
        }

        textureMap.finish();
        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.dispose();
        }
        bakedTextureAtlas = Texture2D.builder().build(textureMap.getTexture());
        textureMap.dispose();
    }

    public void cleanCache() {
        textures.values().forEach(TextureAtlasRegionImpl::cleanCache);
    }

    @Override
    public int getWidth() {
        return bakedTextureAtlas.getWidth();
    }

    @Override
    public int getHeight() {
        return bakedTextureAtlas.getHeight();
    }

    @Override
    public TextureFormat getFormat() {
        return bakedTextureAtlas.getFormat();
    }

    @Override
    public void bind() {
        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.bind();
        }
    }

    @Override
    public void dispose() {
        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.dispose();
            bakedTextureAtlas = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return bakedTextureAtlas == null || bakedTextureAtlas.isDisposed();
    }
}

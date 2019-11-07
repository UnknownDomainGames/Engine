package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.gl.texture.GLTexture2D;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlasImpl implements TextureAtlas {

    private final Map<AssetURL, TextureAtlasRegionImpl> textures = new HashMap<>();

    private GLTexture2D bakedTextureAtlas;

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
        if (bakedTextureAtlas != null) bakedTextureAtlas.dispose();
        bakedTextureAtlas = GLTexture2D.of(textureMap.getTexture());
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
    public float getMinU() {
        return 0;
    }

    @Override
    public float getMinV() {
        return 0;
    }

    @Override
    public float getMaxU() {
        return 1;
    }

    @Override
    public float getMaxV() {
        return 1;
    }

    @Override
    public void bind() {
        if (bakedTextureAtlas != null) bakedTextureAtlas.bind();
    }

    @Override
    public void dispose() {
        if (bakedTextureAtlas != null) bakedTextureAtlas.dispose();
    }
}

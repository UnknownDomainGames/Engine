package engine.graphics.texture;

import engine.client.asset.AssetURL;

import javax.annotation.Nullable;
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
        texture = Texture2D.builder().build(textureMap.getTexture());
        textureMap.dispose();
    }

    public void cleanCache() {
        textures.values().forEach(TextureAtlasRegionImpl::cleanCache);
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public int getId() {
        return texture.getId();
    }

    @Override
    public TextureFormat getFormat() {
        return texture.getFormat();
    }

    @Override
    public boolean isMultiSample() {
        return texture.isMultiSample();
    }

    @Nullable
    @Override
    public Sampler getSampler() {
        return texture.getSampler();
    }

    @Override
    public void bind() {
        if (texture != null) {
            texture.bind();
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return texture == null || texture.isDisposed();
    }
}

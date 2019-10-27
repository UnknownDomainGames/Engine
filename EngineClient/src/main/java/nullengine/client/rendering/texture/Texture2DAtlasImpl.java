package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.gl.texture.GLTexture2D;

import java.util.HashMap;
import java.util.Map;

public class Texture2DAtlasImpl implements Texture2DAtlas {

    private final Map<AssetURL, TextureAtlasPartImpl> textures = new HashMap<>();

    private GLTexture2D bakedTextureAtlas;

    @Override
    public TextureAtlasPart getTexture(AssetURL url) {
        return textures.get(url);
    }

    @Override
    public TextureAtlasPart addTexture(AssetURL url) {
        return textures.computeIfAbsent(url, key -> new TextureAtlasPartImpl(url));
    }

    @Override
    public void reload() {
        int sumWidth = 0;
        int maxHeight = 0;
        for (var part : textures.values()) {
            part.reload();
            var data = part.getData();
            sumWidth += data.getWidth();
            if (data.getHeight() > maxHeight)
                maxHeight = data.getHeight();
        }

        Texture2DBuffer texture2DBuffer = new Texture2DBuffer(sumWidth, maxHeight);
        int offsetX = 0;
        for (var part : textures.values()) {
            var data = part.getData();
            texture2DBuffer.setTexture(offsetX, 0, data);
            part.init(sumWidth, maxHeight, offsetX, 0, data.getWidth(), data.getHeight());
            offsetX += data.getWidth();
        }

        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.dispose();
        }
        bakedTextureAtlas = GLTexture2D.of(texture2DBuffer.getBuffer(), sumWidth, maxHeight);
    }

    public void cleanCache() {
        textures.values().forEach(TextureAtlasPartImpl::cleanCache);
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
        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.bind();
        }
    }

    @Override
    public void dispose() {
        if (bakedTextureAtlas != null) {
            bakedTextureAtlas.dispose();
        }
    }
}

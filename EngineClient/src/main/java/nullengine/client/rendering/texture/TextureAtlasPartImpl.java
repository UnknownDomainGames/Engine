package nullengine.client.rendering.texture;

import nullengine.Platform;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class TextureAtlasPartImpl implements TextureAtlasPart {

    private final AssetURL url;

    private TextureBuffer data;
    private float minU, minV, maxU, maxV;

    public TextureAtlasPartImpl(AssetURL url) {
        this.url = url;
    }

    public void init(float minU, float minV, float maxU, float maxV) {
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    public void init(int textureMapWidth, int textureMapHeight, int offsetX, int offsetY, int width, int height) {
        init((float) offsetX / textureMapWidth,
                (float) offsetY / textureMapHeight,
                (float) (offsetX + width) / textureMapWidth,
                (float) (offsetY + height) / textureMapHeight);
    }

    public void reload() {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(url.toFileLocation());
        if (nativePath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because of missing asset. URL: " + url);
        }

        try (var channel = Files.newByteChannel(nativePath.get())) {
            var bytes = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(bytes);
            bytes.flip();
            data = TextureBuffer.create(bytes);
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because of catching exception. URL: " + url);
        }
    }

    public void cleanCache() {
        data = null;
    }

    @Override
    public AssetURL getUrl() {
        return url;
    }

    @Override
    public TextureBuffer getData() {
        return data;
    }

    @Override
    public float getMinU() {
        return minU;
    }

    @Override
    public float getMinV() {
        return minV;
    }

    @Override
    public float getMaxU() {
        return maxU;
    }

    @Override
    public float getMaxV() {
        return maxV;
    }
}

package nullengine.client.rendering.texture;

import nullengine.Platform;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class TextureAtlasRegionImpl implements TextureAtlasRegion {

    private final AssetURL url;

    private Texture2DBuffer data;
    private TextureAtlasBuilder.UVResult uv;

    public TextureAtlasRegionImpl(AssetURL url) {
        this.url = url;
    }

    public void setUv(TextureAtlasBuilder.UVResult uv) {
        this.uv = uv;
    }

    public void reload() {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(url.toFileLocation("texture", ".png"));
        if (nativePath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because of missing asset. Path: " + url.toFileLocation("texture", ".png"));
        }

        try (var channel = Files.newByteChannel(nativePath.get())) {
            var bytes = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(bytes);
            bytes.flip();
            data = Texture2DBuffer.create(bytes);
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because of catching exception. Path: " + url.toFileLocation("texture", ".png"));
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
    public Texture2DBuffer getData() {
        return data;
    }

    @Override
    public float getMinU() {
        return uv.getMinU();
    }

    @Override
    public float getMinV() {
        return uv.getMinV();
    }

    @Override
    public float getMaxU() {
        return uv.getMaxU();
    }

    @Override
    public float getMaxV() {
        return uv.getMaxV();
    }
}

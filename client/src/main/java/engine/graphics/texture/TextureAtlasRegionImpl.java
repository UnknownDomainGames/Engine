package engine.graphics.texture;

import engine.Platform;
import engine.client.asset.AssetURL;
import engine.client.asset.exception.AssetLoadException;
import engine.graphics.image.BufferedImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class TextureAtlasRegionImpl implements TextureAtlasRegion {

    private final AssetURL url;

    private BufferedImage data;
    private TextureAtlasBuilder.TexCoord uv;

    public TextureAtlasRegionImpl(AssetURL url) {
        this.url = url;
    }

    public void setUv(TextureAtlasBuilder.TexCoord uv) {
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
            data = BufferedImage.load(bytes);
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
    public BufferedImage getData() {
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

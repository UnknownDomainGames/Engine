package nullengine.client.rendering.texture;

import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineTextureManager implements TextureManager, AssetProvider<GLTexture> {

    private final List<Asset<GLTexture>> assets = new ArrayList<>();
    private final Map<TextureAtlasName, TextureAtlasImpl> texturesAtlases = new HashMap<>();

    private final GLTexture whiteTexture;

    private AssetSourceManager sourceManager;

    public EngineTextureManager() {
        this.whiteTexture = getTextureDirect(new TextureBuffer(2, 2, 0xffffffff));
    }

    @Override
    public GLTexture getWhiteTexture() {
        return whiteTexture;
    }

    @Override
    public GLTexture getTextureDirect(TextureBuffer buffer) {
        return GLTexture.of(buffer.getWidth(), buffer.getHeight(), buffer.getBuffer());
    }

    @Override
    public TextureAtlas getTextureAtlas(TextureAtlasName type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlasImpl());
    }

    @Override
    public void init(AssetManager manager, AssetType<GLTexture> type) {
        sourceManager = manager.getSourceManager();
        manager.getReloadManager().addListener(new AssetReloadListener().name("Texture").befores("CleanTextureCache").runnable(this::reload));
        manager.getReloadManager().addListener(new AssetReloadListener().name("CleanTextureCache").runnable(this::cleanCache));
    }

    @Override
    public void register(Asset<GLTexture> asset) {
        assets.add(asset);
    }

    @Override
    public void unregister(Asset<GLTexture> asset) {
        var glTexture = asset.get();
        if (glTexture != null) {
            glTexture.dispose();
        }
        assets.remove(asset);
    }

    private void reload() {
        assets.forEach(asset -> {
            var glTexture = asset.get();
            if (glTexture != null) {
                glTexture.dispose();
            }
            asset.reload();
        });
        texturesAtlases.values().forEach(TextureAtlas::reload);
    }

    private void cleanCache() {
        texturesAtlases.values().forEach(TextureAtlasImpl::cleanCache);
    }

    @Nonnull
    @Override
    public GLTexture loadDirect(AssetURL url) {
        var localPath = sourceManager.getPath(url.toFileLocation("texture", ".png"));
        if (localPath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because missing asset. Path: " + url.toFileLocation("texture", ".png"));
        }

        try (var channel = Files.newByteChannel(localPath.get())) {
            var buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(buffer);
            buffer.flip();
            return getTextureDirect(TextureBuffer.create(buffer));
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because catch exception. Path: " + url.toFileLocation("texture", ".png"), e);
        }
    }

    @Override
    public void dispose() {
        assets.forEach(asset -> {
            var glTexture = asset.get();
            if (glTexture != null) {
                glTexture.dispose();
            }
        });
    }
}

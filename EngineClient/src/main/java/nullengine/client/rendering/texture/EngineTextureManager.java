package nullengine.client.rendering.texture;

import nullengine.client.asset.*;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.gl.texture.GLTexture2D;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineTextureManager implements TextureManager, AssetProvider<GLTexture2D> {

    private final List<Asset<GLTexture2D>> assets = new ArrayList<>();
    private final Map<TextureAtlasName, TextureAtlasImpl> texturesAtlases = new HashMap<>();

    private final GLTexture2D whiteTexture;

    private AssetSourceManager sourceManager;

    public EngineTextureManager() {
        this.whiteTexture = GLTexture2D.of(new Texture2DBuffer(2, 2, 0xffffffff));
    }

    @Override
    public GLTexture2D getWhiteTexture() {
        return whiteTexture;
    }

    @Override
    public TextureAtlas getTextureAtlas(TextureAtlasName type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlasImpl());
    }

    @Override
    public void init(AssetManager manager, AssetType<GLTexture2D> type) {
        sourceManager = manager.getSourceManager();
        manager.getReloadManager().addListener(AssetReloadListener.builder().name("Texture").before("CleanTextureCache").runnable(this::reload).build());
        manager.getReloadManager().addListener(AssetReloadListener.builder().name("CleanTextureCache").runnable(this::cleanCache).build());
    }

    @Override
    public void register(Asset<GLTexture2D> asset) {
        assets.add(asset);
    }

    @Override
    public void unregister(Asset<GLTexture2D> asset) {
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
    public GLTexture2D loadDirect(AssetURL url) {
        var localPath = sourceManager.getPath(url.toFileLocation("texture", ".png"));
        if (localPath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because missing asset. Path: " + url.toFileLocation("texture", ".png"));
        }

        try (var channel = Files.newByteChannel(localPath.get())) {
            var buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(buffer);
            buffer.flip();
            return GLTexture2D.of(Texture2DBuffer.create(buffer));
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

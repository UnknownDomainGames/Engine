package engine.client.asset.provider;

import com.google.common.collect.Maps;
import engine.client.asset.*;
import engine.client.asset.exception.AssetLoadException;
import engine.client.asset.reloading.AssetReloadListener;
import engine.client.asset.source.AssetSourceManager;
import engine.graphics.image.BufferedImage;
import engine.graphics.texture.Texture2D;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TextureAssetProvider implements AssetProvider<Texture2D> {

    private final List<Asset<Texture2D>> assets = new ArrayList<>();
    private final Map<AssetURL, Texture2D> loadedTextures = Maps.newConcurrentMap();

    private final Map<AssetURL, Texture2D> buildinTextures;

    private final Texture2D.Builder builder;

    private AssetType<Texture2D> type;
    private AssetSourceManager sourceManager;

    public TextureAssetProvider() {
        this.buildinTextures = Map.of(AssetURL.of("buildin", "white"), Texture2D.white());
        this.builder = Texture2D.builder();
    }

    @Override
    public void init(AssetManager manager, AssetType<Texture2D> type) {
        this.type = type;
        this.sourceManager = manager.getSourceManager();
        manager.getReloadManager().addListener(AssetReloadListener.builder().name("Texture").runnable(this::reload).build());
    }

    @Override
    public void register(Asset<Texture2D> asset) {
        assets.add(asset);
    }

    @Override
    public void unregister(Asset<Texture2D> asset) {
        assets.remove(asset);
    }

    private void reload() {
        loadedTextures.forEach((assetURL, texture2D) -> texture2D.dispose());
        loadedTextures.clear();
        assets.forEach(Asset::reload);
    }

    @Nonnull
    @Override
    public Texture2D loadDirect(AssetURL url) {
        if ("buildin".equals(url.getDomain())) return buildinTextures.get(url);
        return loadedTextures.computeIfAbsent(url, this::load);
    }

    private Texture2D load(AssetURL url) {
        var localPath = sourceManager.getPath(url.toFileLocation(type));
        if (localPath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because missing asset. Path: " + url.toFileLocation("texture", ".png"));
        }

        try (var channel = Files.newByteChannel(localPath.get())) {
            var buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(buffer);
            buffer.flip();
            return builder.build(BufferedImage.load(buffer));
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because catch exception. Path: " + url.toFileLocation("texture", ".png"), e);
        }
    }

    @Override
    public void dispose() {
        new ArrayList<>(assets).forEach(this::unregister);
    }
}

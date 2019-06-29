package nullengine.client.rendering.texture;

import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.Platform;
import nullengine.client.asset.AssetPath;
import nullengine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EngineTextureManager implements TextureManager {

    private final Map<AssetPath, MutableValue<GLTexture>> textures = new HashMap<>();

    private final Map<TextureAtlasName, TextureAtlasImpl> texturesAtlases = new HashMap<>();

    private final GLTexture whiteTexture;

    public EngineTextureManager() {
        this.whiteTexture = getTextureDirect(new TextureBuffer(2, 2, 0xffffffff));
    }

    @Override
    public ObservableValue<GLTexture> getTexture(AssetPath path) {
        return textures.computeIfAbsent(path, key -> new SimpleMutableObjectValue<>(getTextureDirect(path)));
    }

    @Override
    public GLTexture getTextureDirect(AssetPath path) {
        Optional<Path> localPath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(path);
        if (localPath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because missing asset. Path: " + path);
        }

        try (var channel = Files.newByteChannel(localPath.get())) {
            var buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
            channel.read(buffer);
            buffer.flip();
            return getTextureDirect(TextureBuffer.create(buffer));
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because catch exception. Path: " + path, e);
        }
    }

    @Override
    public GLTexture getTextureDirect(TextureBuffer buffer) {
        return GLTexture.of(buffer.getWidth(), buffer.getHeight(), buffer.getBuffer());
    }

    @Override
    public TextureAtlasPart addTextureToAtlas(AssetPath path, TextureAtlasName type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlasImpl()).addTexture(path);
    }

    @Override
    public TextureAtlas getTextureAtlas(TextureAtlasName type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlasImpl());
    }

    @Override
    public void reloadTextureAtlas(TextureAtlasName type) {
        try {
            if (texturesAtlases.containsKey(type)) {
                texturesAtlases.get(type).reload();
            }
        } catch (IOException e) {
            Platform.getLogger().warn(String.format("Cannot reload texture atlas %s.", type), e);
        }
    }

    @Override
    public void reload() {
        for (Map.Entry<AssetPath, MutableValue<GLTexture>> entry : textures.entrySet()) {
            entry.getValue().ifPresent(GLTexture::dispose);
            entry.getValue().setValue(getTextureDirect(entry.getKey()));
        }

        texturesAtlases.keySet().forEach(this::reloadTextureAtlas);
    }

    @Override
    public GLTexture getWhiteTexture() {
        return whiteTexture;
    }
}

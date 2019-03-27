package unknowndomain.engine.client.rendering.texture;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import de.matthiasmann.twl.utils.PNGDecoder;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EngineTextureManager implements TextureManager {

    private final Map<AssetPath, MutableValue<GLTexture>> textures = new HashMap<>();

    private final Map<TextureType, TextureAtlas> texturesAtlases = new HashMap<>();

    @Override
    public ObservableValue<GLTexture> getTexture(AssetPath path) {
        return textures.computeIfAbsent(path, key -> new SimpleMutableObjectValue<>(getTextureDirect(path)));
    }

    @Override
    public GLTexture getTextureDirect(AssetPath path) {
        Optional<Path> localPath = Platform.getEngineClient().getAssetManager().getPath(path);
        if (localPath.isEmpty()) {
            throw new AssetLoadException("Cannot load texture because missing asset. Path: " + path);
        }

        try (InputStream inputStream = Files.newInputStream(localPath.get())) {
            PNGDecoder decoder = new PNGDecoder(inputStream);
            TextureBuffer buf = TextureBuffer.create(decoder);
            return GLTexture.of(buf.getWidth(), buf.getHeight(), buf.getBuffer());
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load texture because catch exception. Path: " + path, e);
        }
    }

    @Override
    public TextureUV addTextureToAtlas(AssetPath path, TextureType type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlas()).addTexture(path);
    }

    @Override
    public ObservableValue<GLTexture> getTextureAtlas(TextureType type) {
        return texturesAtlases.computeIfAbsent(type, key -> new TextureAtlas()).getTexture();
    }

    @Override
    public void reloadTextureAtlas(TextureType type) {
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
}

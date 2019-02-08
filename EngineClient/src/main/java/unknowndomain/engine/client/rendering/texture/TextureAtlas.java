package unknowndomain.engine.client.rendering.texture;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TextureAtlas {

    private final Map<AssetPath, TextureUVImpl> textures = new HashMap<>();
    private final MutableValue<GLTexture> bakedTextureAtlas = new SimpleMutableObjectValue<>();

    public TextureUV addTexture(AssetPath path) {
        return textures.computeIfAbsent(path, key -> new TextureUVImpl());
    }

    public ObservableValue<GLTexture> getTexture() {
        return bakedTextureAtlas.toImmutable();
    }

    public void reload() throws IOException {
        List<Pair<TextureBuffer, TextureUVImpl>> loadedTextures = new LinkedList<>();
        for (Map.Entry<AssetPath, TextureUVImpl> entry : textures.entrySet()) {
            loadedTextures.add(Pair.of(loadTextureBuffer(entry.getKey()), entry.getValue()));
        }
        int sumWidth = 0;
        int maxHeight = 0;
        for (Pair<TextureBuffer, TextureUVImpl> pair : loadedTextures) {
            TextureBuffer source = pair.getLeft();
            sumWidth += source.getWidth();
            if (source.getHeight() > maxHeight)
                maxHeight = source.getHeight();
        }
        TextureBuffer textureBuffer = new TextureBuffer(sumWidth, maxHeight);
        int offsetX = 0;
        for (Pair<TextureBuffer, TextureUVImpl> pair : loadedTextures) {
            TextureBuffer source = pair.getLeft();
            textureBuffer.setTexture(offsetX, 0, source);
            pair.getRight().init(sumWidth, maxHeight, offsetX, 0, source.getWidth(), source.getHeight());
            offsetX += source.getWidth();
        }
        bakedTextureAtlas.ifPresent(GLTexture::dispose);
        bakedTextureAtlas.setValue(GLTexture.of(sumWidth, maxHeight, textureBuffer.getBuffer()));
    }

    private TextureBuffer loadTextureBuffer(AssetPath path) throws IOException {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getPath(path);
        if (nativePath.isPresent()) {
            try (InputStream inputStream = Files.newInputStream(nativePath.get())) {
                PNGDecoder decoder = new PNGDecoder(inputStream);
                return TextureBuffer.create(decoder);
            }
        }
        throw new AssetLoadException("Cannot load texture because missing asset. Path: " + path);
    }
}

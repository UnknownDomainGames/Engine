package nullengine.client.rendering.texture;

import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.Platform;
import nullengine.client.asset.AssetPath;
import nullengine.client.asset.exception.AssetLoadException;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TextureAtlasImpl implements TextureAtlas {

    private final Map<AssetPath, TextureAtlasPartImpl> textures = new HashMap<>();
    private final MutableValue<GLTexture> bakedTextureAtlas = new SimpleMutableObjectValue<>();

    @Override
    public TextureAtlasPart addTexture(AssetPath path) {
        return textures.computeIfAbsent(path, key -> new TextureAtlasPartImpl());
    }

    @Override
    public ObservableValue<GLTexture> getTexture() {
        return bakedTextureAtlas.toImmutable();
    }

    public void reload() throws IOException {
        List<Pair<TextureBuffer, TextureAtlasPartImpl>> loadedTextures = new LinkedList<>();
        for (Map.Entry<AssetPath, TextureAtlasPartImpl> entry : textures.entrySet()) {
            loadedTextures.add(Pair.of(loadTextureBuffer(entry.getKey()), entry.getValue()));
        }
        int sumWidth = 0;
        int maxHeight = 0;
        for (Pair<TextureBuffer, TextureAtlasPartImpl> pair : loadedTextures) {
            TextureBuffer source = pair.getLeft();
            sumWidth += source.getWidth();
            if (source.getHeight() > maxHeight)
                maxHeight = source.getHeight();
        }
        TextureBuffer textureBuffer = new TextureBuffer(sumWidth, maxHeight);
        int offsetX = 0;
        for (Pair<TextureBuffer, TextureAtlasPartImpl> pair : loadedTextures) {
            TextureBuffer source = pair.getLeft();
            textureBuffer.setTexture(offsetX, 0, source);
            pair.getRight().init(sumWidth, maxHeight, offsetX, 0, source.getWidth(), source.getHeight());
            offsetX += source.getWidth();
        }
        bakedTextureAtlas.ifPresent(GLTexture::dispose);
        bakedTextureAtlas.setValue(GLTexture.of(sumWidth, maxHeight, textureBuffer.getBuffer()));
    }

    private TextureBuffer loadTextureBuffer(AssetPath path) throws IOException {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(path);
        if (nativePath.isPresent()) {
            try (var channel = Files.newByteChannel(nativePath.get())) {
                var filebuf = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
                channel.read(filebuf);
                filebuf.flip();
                return TextureBuffer.create(filebuf);
            }
        }
        throw new AssetLoadException("Cannot load texture because missing asset. Path: " + path);
    }
}

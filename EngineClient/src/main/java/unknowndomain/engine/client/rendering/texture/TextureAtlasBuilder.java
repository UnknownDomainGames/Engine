package unknowndomain.engine.client.rendering.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TextureAtlasBuilder {

    public GLTexture buildFromResources(List<Pair<AssetPath, TextureUVImpl>> textures) throws IOException {
        List<Pair<TextureBuffer, TextureUVImpl>> loadedTextures = new LinkedList<>();
        for (Pair<AssetPath, TextureUVImpl> pair : textures) {
            loadedTextures.add(Pair.of(getBufferFromResource(pair.getLeft()), pair.getRight()));
        }
        return build(loadedTextures);
    }

    public TextureBuffer getBufferFromResource(AssetPath path) throws IOException {
        Optional<Path> nativePath = Platform.getEngineClient().getAssetSourceManager().getPath(path);
        if (nativePath.isPresent()) {
            try (InputStream inputStream = Files.newInputStream(nativePath.get())) {
                PNGDecoder decoder = new PNGDecoder(inputStream);
                return TextureBuffer.create(decoder);
            }
        }
        throw new AssetLoadException("Cannot load texture. Path: " + path);
    }

    public GLTexture build(List<Pair<TextureBuffer, TextureUVImpl>> textures) {
        int sumWidth = 0;
        int maxHeight = 0;
        for (Pair<TextureBuffer, TextureUVImpl> pair : textures) {
            TextureBuffer source = pair.getLeft();
            sumWidth += source.getWidth();
            if (source.getHeight() > maxHeight)
                maxHeight = source.getHeight();
        }
        TextureBuffer textureBuffer = new TextureBuffer(sumWidth, maxHeight);
        int offsetX = 0;
        for (Pair<TextureBuffer, TextureUVImpl> pair : textures) {
            TextureBuffer source = pair.getLeft();
            textureBuffer.setTexture(offsetX, 0, source);
            pair.getRight().init(sumWidth, maxHeight, offsetX, 0, source.getWidth(), source.getHeight());
            offsetX += source.getWidth();
        }
        return GLTexture.of(sumWidth, maxHeight, textureBuffer.getBuffer());
    }
}

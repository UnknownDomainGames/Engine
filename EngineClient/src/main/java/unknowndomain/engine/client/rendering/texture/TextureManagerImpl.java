package unknowndomain.engine.client.rendering.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.ExecutionException;

public class TextureManagerImpl implements TextureManager {

    private final Cache<AssetPath, GLTexture> cachedTextures = CacheBuilder.newBuilder().weakValues().removalListener(notification -> ((GLTexture) notification.getValue()).dispose()).build();

    private final Map<TextureType, List<Pair<AssetPath, TextureUVImpl>>> texturesAltas = new HashMap<>();
    private final Map<TextureType, GLTexture> bakedTextureAtlases = new HashMap<>();
    private final TextureAtlasBuilder textureAtlasBuilder = new TextureAtlasBuilder();

    @Override
    public GLTexture getTexture(AssetPath path, boolean reload) {
        if (!reload) {
            GLTexture texture = cachedTextures.getIfPresent(path);
            if (texture != null) {
                return texture;
            }
        }

        try {
            return cachedTextures.get(path, () -> {
                Optional<Path> nativePath = Platform.getEngineClient().getAssetManager().getPath(path);
                if (nativePath.isPresent()) {
                    try (InputStream inputStream = Files.newInputStream(nativePath.get())) {
                        PNGDecoder decoder = new PNGDecoder(inputStream);
                        TextureBuffer buf = TextureBuffer.create(decoder);
                        return GLTexture.of(buf.getWidth(), buf.getHeight(), buf.getBuffer());
                    }
                }
                throw new AssetLoadException("Cannot load texture. Path: " + path);
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public TextureUV registerToAtlas(AssetPath path, TextureType type) {
        TextureUVImpl texture = new TextureUVImpl();
        texturesAltas.computeIfAbsent(type, key -> new ArrayList<>()).add(Pair.of(path, texture));
        return texture;
    }

    public GLTexture getTextureAtlas(TextureType type) {
        return bakedTextureAtlases.get(type);
    }

    @Override
    public GLTexture initTextureAtlas(TextureType type) {
        return bakedTextureAtlases.computeIfAbsent(type, key -> {
            try {
                return textureAtlasBuilder.buildFromResources(texturesAltas.getOrDefault(type, Collections.emptyList()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

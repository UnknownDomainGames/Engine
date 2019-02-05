package unknowndomain.engine.client.rendering.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.resource.ResourcePath;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TextureManagerImpl implements TextureManager {

    private final Cache<AssetPath, GLTexture> cachedTextures = CacheBuilder.newBuilder().weakValues().removalListener(notification -> ((GLTexture) notification.getValue()).dispose()).build();

    private final Map<TextureType, List<Pair<ResourcePath, TextureUVImpl>>> texturesAltas = new HashMap<>();
    private final Map<TextureType, GLTexture> bakedTextureAtlases = new HashMap<>();
    private final TextureAtlasBuilder textureAtlasBuilder = new TextureAtlasBuilder();

    public TextureUV registerToAtlas(ResourcePath path, TextureType type) {
        TextureUVImpl texture = new TextureUVImpl();
        texturesAltas.computeIfAbsent(type, key -> new ArrayList<>()).add(Pair.of(path, texture));
        return texture;
    }

    @Override
    public GLTexture getTexture(AssetPath path, boolean reload) {
        if (!reload) {
            GLTexture texture = cachedTextures.getIfPresent(path);
            if (texture != null) {
                return texture;
            }
        }

        return cachedTextures.get(path, key -> {
            try (InputStream inputStream =) {
                PNGDecoder decoder = new PNGDecoder(inputStream);
                TextureBuffer buf = TextureBuffer.create(decoder);
                return GLTexture.of(buf.getWidth(), buf.getHeight(), buf.getBuffer());
            }
        });
    }

    @Override
    public TextureUV registerToAtlas(AssetPath path, TextureType type) {
        return null;
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

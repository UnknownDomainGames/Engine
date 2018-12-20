package unknowndomain.engine.client.rendering.texture;

import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.client.resource.ResourcePath;

import java.io.IOException;
import java.util.*;

public class TextureManagerImpl implements TextureManager {

    private final Map<TextureType, List<Pair<ResourcePath, TextureUVImpl>>> textures = new HashMap<>();
    private final Map<TextureType, GLTexture> bakedTextureAtlases = new HashMap<>();
    private final TextureAtlasBuilder textureAtlasBuilder = new TextureAtlasBuilder();

    public TextureUV register(ResourcePath path, TextureType type) {
        TextureUVImpl texture = new TextureUVImpl();
        textures.computeIfAbsent(type, key -> new ArrayList<>()).add(Pair.of(path, texture));
        return texture;
    }

    public GLTexture getTextureAtlas(TextureType type) {
        return bakedTextureAtlases.get(type);
    }

    @Override
    public GLTexture initTextureAtlas(TextureType type) {
        return bakedTextureAtlases.computeIfAbsent(type, key -> {
            try {
                return textureAtlasBuilder.buildFromResources(textures.getOrDefault(type, Collections.emptyList()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

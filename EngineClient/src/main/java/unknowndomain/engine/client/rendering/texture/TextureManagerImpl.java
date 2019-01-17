package unknowndomain.engine.client.rendering.texture;

import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.resource.ResourcePath;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

public class TextureManagerImpl implements TextureManager {

    private final Map<ResourcePath, GLTexture> textures = new HashMap<>();
    private final Map<TextureType, List<Pair<ResourcePath, TextureUVImpl>>> texturesAltas = new HashMap<>();
    private final Map<TextureType, GLTexture> bakedTextureAtlases = new HashMap<>();
    private final TextureAtlasBuilder textureAtlasBuilder = new TextureAtlasBuilder();

    @Override
    @Nullable
    public GLTexture register(ResourcePath path){
        TextureBuffer buf;
        try {
            buf = textureAtlasBuilder.getBufferFromResource(path);
            GLTexture texture = GLTexture.of(buf.getWidth(), buf.getHeight(), buf.getBuffer());
            textures.put(path, texture);
            return texture;
        } catch (IOException e) {
            Engine.getLogger().warn(String.format("cannot load texture %s!", path), e);
        }
        return null;
    }

    @Override
    public GLTexture getTexture(ResourcePath path){
        if(!textures.containsKey(path)){
            register(path);
        }
        return textures.get(path);
    }

    public TextureUV registerToAtlas(ResourcePath path, TextureType type) {
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

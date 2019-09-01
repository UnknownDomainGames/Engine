package nullengine.client.rendering.model.data;

import com.google.gson.annotations.JsonAdapter;
import nullengine.client.rendering.texture.TextureAtlasPart;
import org.joml.Vector4f;

public final class Face {
    public String texture;
    public transient TextureAtlasPart textureInstance;
    public Vector4f uv;
    @JsonAdapter(CullFacesDeserializer.class)
    public int cullFaces;
}

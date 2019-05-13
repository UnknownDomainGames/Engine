package unknowndomain.engine.client.rendering.model.voxel;

import org.joml.Vector3fc;
import org.joml.Vector4fc;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.texture.TextureAtlasPart;

import java.util.List;
import java.util.Map;

public class ModelData {

    AssetPath parent;
    Map<String, String> textures;
    List<Element> elements;

    static class Element {
        static class Cube extends Element {
            Vector3fc from;
            Vector3fc to;
            Face[] faces = new Face[6];

            static class Face {
                Texture texture;
                boolean[] cullFace = new boolean[6];
            }
        }
    }

    static class Texture {
        TextureAtlasPart textureAtlasPart;
        Vector4fc uv;
    }
}

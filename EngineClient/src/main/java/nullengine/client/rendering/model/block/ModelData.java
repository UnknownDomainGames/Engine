package nullengine.client.rendering.model.block;

import com.google.gson.JsonArray;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.TextureAtlasPart;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.List;
import java.util.Map;

class ModelData {

    AssetURL url;
    Map<String, String> textures;
    List<Element> elements;
    JsonArray rawElements;
    boolean[] fullFaces = new boolean[6];

    static class Element {
        static class Cube extends Element {
            Vector3fc from;
            Vector3fc to;
            Face[] faces;

            static class Face {
                String texture;
                TextureAtlasPart resolvedTexture;
                Vector4fc uv;
                byte cullFaces;
            }
        }
    }
}

package unknowndomain.engine.client.rendering.model.umodel;

import org.joml.Vector3fc;
import org.joml.Vector4fc;

import java.util.List;
import java.util.Map;

public class Model {

    String parent;
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
        String name;
        Vector4fc uv;
    }
}

package unknowndomain.engine.client.rendering.model.voxel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import unknowndomain.engine.util.Facing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {

    public static Model load(JsonObject json) {
        Model model = new Model();
        if (json.has("parent")) {
            model.parent = json.get("parent").getAsString();
        }
        if (json.has("textures")) {
            model.textures = loadTextures(json.getAsJsonObject("textures"));
        }
        if (json.has("elements")) {
            model.elements = loadElements(json.getAsJsonArray("elements"));
        }
        return model;
    }

    private static Map<String, String> loadTextures(JsonObject json) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Map.copyOf(map);
    }

    private static List<Model.Element> loadElements(JsonArray json) {
        List<Model.Element> elements = new ArrayList<>();
        for (JsonElement jsonElement : json) {
            elements.add(loadCube(jsonElement.getAsJsonObject()));
        }
        return elements;
    }

    private static Model.Element.Cube loadCube(JsonObject json) {
        Model.Element.Cube cube = new Model.Element.Cube();
        cube.from = loadVector3f(json.getAsJsonArray("from"));
        cube.to = loadVector3f(json.getAsJsonArray("to"));

        return cube;
    }

    private static Model.Element.Cube.Face[] loadFaces(JsonObject json) {
        Model.Element.Cube.Face[] faces = new Model.Element.Cube.Face[6];
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            faces[Facing.valueOf(entry.getKey().toUpperCase()).index] = loadFace(entry.getValue().getAsJsonObject());
        }
        return faces;
    }

    private static Model.Element.Cube.Face loadFace(JsonObject json) {
        Model.Element.Cube.Face face = new Model.Element.Cube.Face();
        face.texture = loadTexture(json.getAsJsonObject("texture"));
        face.cullFace = loadCullFace(json.get("cullFace"));
        return face;
    }

    private static Vector3fc loadVector3f(JsonArray json) {
        return new Vector3f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat());
    }

    private static Model.Texture loadTexture(JsonObject json) {
        Model.Texture texture = new Model.Texture();
        texture.name = json.get("name").getAsString();
        texture.uv = loadVector4f(json.getAsJsonArray("uv"));
        return texture;
    }

    private static Vector4fc loadVector4f(JsonArray json) {
        return new Vector4f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat(), json.get(3).getAsFloat());
    }

    private static boolean[] loadCullFace(JsonElement json) {
        boolean[] cullFaces = new boolean[6];
        if (json.isJsonArray()) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                cullFaces[Facing.valueOf(jsonElement.getAsString().toUpperCase()).index] = true;
            }
        } else {
            cullFaces[Facing.valueOf(json.getAsString().toUpperCase()).index] = true;
        }
        return cullFaces;
    }
}

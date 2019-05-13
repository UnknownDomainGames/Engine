package unknowndomain.engine.client.rendering.model.voxel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.texture.TextureAtlas;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.util.JsonUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {

    public final TextureAtlas textureAtlas;

    public ModelLoader(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public ModelData load(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return load(JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
        }
    }

    public ModelData load(JsonObject json) {
        ModelData modelData = new ModelData();
        if (json.has("parent")) {
            modelData.parent = AssetPath.of(json.get("parent").getAsString());
        }
        if (json.has("textures")) {
            modelData.textures = loadTextures(json.getAsJsonObject("textures"));
        }
        if (json.has("elements")) {
            modelData.elements = loadElements(json.getAsJsonArray("elements"));
        }
        return modelData;
    }

    private Map<String, String> loadTextures(JsonObject json) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Map.copyOf(map);
    }

    private List<ModelData.Element> loadElements(JsonArray json) {
        List<ModelData.Element> elements = new ArrayList<>();
        for (JsonElement jsonElement : json) {
            elements.add(loadCube(jsonElement.getAsJsonObject()));
        }
        return elements;
    }

    private ModelData.Element.Cube loadCube(JsonObject json) {
        ModelData.Element.Cube cube = new ModelData.Element.Cube();
        cube.from = loadVector3f(json.getAsJsonArray("from"));
        cube.to = loadVector3f(json.getAsJsonArray("to"));
        cube.faces = loadFaces(json);
        return cube;
    }

    private ModelData.Element.Cube.Face[] loadFaces(JsonObject json) {
        ModelData.Element.Cube.Face[] faces = new ModelData.Element.Cube.Face[6];
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            faces[Facing.valueOf(entry.getKey().toUpperCase()).index] = loadFace(entry.getValue().getAsJsonObject());
        }
        return faces;
    }

    private ModelData.Element.Cube.Face loadFace(JsonObject json) {
        ModelData.Element.Cube.Face face = new ModelData.Element.Cube.Face();
        face.texture = loadTexture(json.getAsJsonObject("texture"));
        face.cullFace = loadCullFace(json.get("cullFace"));
        return face;
    }

    private Vector3fc loadVector3f(JsonArray json) {
        return new Vector3f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat());
    }

    private ModelData.Texture loadTexture(JsonObject json) {
        ModelData.Texture texture = new ModelData.Texture();
        texture.textureAtlasPart = textureAtlas.addTexture(AssetPath.of(json.get("name").getAsString()));
        texture.uv = loadVector4f(json.getAsJsonArray("uv"));
        return texture;
    }

    private Vector4fc loadVector4f(JsonArray json) {
        return new Vector4f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat(), json.get(3).getAsFloat());
    }

    private boolean[] loadCullFace(JsonElement json) {
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

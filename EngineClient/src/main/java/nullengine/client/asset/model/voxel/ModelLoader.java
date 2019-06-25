package nullengine.client.asset.model.voxel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.client.asset.AssetPath;
import nullengine.util.Facing;
import nullengine.util.JsonUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModelLoader {

    private final VoxelModelManager modelManager;

    public ModelLoader(VoxelModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public ModelData load(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return load(JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
        }
    }

    public ModelData load(JsonObject json) {
        ModelData modelData = new ModelData();
        ModelData parent = null;
        if (json.has("parent")) {
            parent = modelManager.getModelData(AssetPath.of(json.get("parent").getAsString()));
        }
        if (json.has("textures")) {
            modelData.textures = loadTextures(json.getAsJsonObject("textures"), parent != null && parent.textures != null ? parent.textures : Map.of());
        }
        JsonArray elements = parent != null ? parent.rawElements : json.getAsJsonArray("elements");
        modelData.elements = loadElements(elements);
        modelData.rawElements = elements;
        return modelData;
    }

    private Map<String, String> loadTextures(JsonObject json, Map<String, String> parent) {
        if (json == null) {
            return Map.copyOf(parent);
        }

        Map<String, String> map = new HashMap<>(parent);
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return Map.copyOf(map);
    }

    private List<ModelData.Element> loadElements(JsonArray json) {
        if (json == null) {
            return List.of();
        }

        List<ModelData.Element> elements = new ArrayList<>();
        for (JsonElement jsonElement : json) {
            elements.add(loadCube(jsonElement.getAsJsonObject()));
        }
        return List.copyOf(elements);
    }

    private ModelData.Element.Cube loadCube(JsonObject json) {
        ModelData.Element.Cube cube = new ModelData.Element.Cube();
        cube.from = loadVector3f(json.getAsJsonArray("from"));
        cube.to = loadVector3f(json.getAsJsonArray("to"));
        cube.faces = loadFaces(json.getAsJsonObject("faces"));
        return cube;
    }

    private ModelData.Element.Cube.Face[] loadFaces(JsonObject json) {
        ModelData.Element.Cube.Face[] faces = new ModelData.Element.Cube.Face[6];
        if (json != null) {
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                Facing facing = Facing.valueOf(entry.getKey().toUpperCase());
                faces[facing.index] = loadFace(entry.getValue().getAsJsonObject(), facing);
            }
        }
        return faces;
    }

    private ModelData.Element.Cube.Face loadFace(JsonObject json, Facing facing) {
        ModelData.Element.Cube.Face face = new ModelData.Element.Cube.Face();
        face.texture = json.get("texture").getAsString();
        face.uv = loadVector4f(json.getAsJsonArray("uv"));
        face.cullFace = loadCullFace(json.get("cullFace"), facing);
        return face;
    }

    private Vector3fc loadVector3f(JsonArray json) {
        return new Vector3f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat());
    }

    private static final Vector4fc DEFAULT_UV = new Vector4f(0, 0, 1, 1);

    private Vector4fc loadVector4f(JsonArray json) {
        return json == null ? DEFAULT_UV :
                new Vector4f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat(), json.get(3).getAsFloat());
    }

    private boolean[] loadCullFace(JsonElement json, Facing defaultCullFace) {
        boolean[] cullFaces = new boolean[6];
        if (json != null) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                cullFaces[Facing.valueOf(jsonElement.getAsString().toUpperCase()).index] = true;
            }
        } else {
            cullFaces[defaultCullFace.index] = true;
        }
        return cullFaces;
    }
}

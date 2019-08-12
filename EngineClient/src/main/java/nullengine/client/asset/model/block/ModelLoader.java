package nullengine.client.asset.model.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.client.asset.AssetType;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.exception.AssetNotFoundException;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.util.Direction;
import nullengine.util.JsonUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class ModelLoader {

    private final BlockModelManager modelManager;
    private final AssetSourceManager sourceManager;
    private final AssetType<BlockModel> type;

    public ModelLoader(BlockModelManager modelManager, AssetSourceManager sourceManager, AssetType<BlockModel> type) {
        this.modelManager = modelManager;
        this.sourceManager = sourceManager;
        this.type = type;
    }

    public ModelData load(AssetURL url) {
        Optional<Path> path = sourceManager.getPath(url.toFileLocation(type));
        if (path.isEmpty())
            throw new AssetNotFoundException(url);
        try {
            try (Reader reader = Files.newBufferedReader(path.get())) {
                return load(url, JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
            }
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model.", e);
        }
    }

    public ModelData load(AssetURL url, JsonObject json) {
        ModelData modelData = new ModelData();
        modelData.url = url;
        modelData.fullFaces = loadFullFace(json.getAsJsonObject("fullFaces"));
        ModelData parent = null;
        if (json.has("parent")) {
            parent = modelManager.getModelData(AssetURL.fromString(url, json.get("parent").getAsString()));
        }
        if (json.has("textures")) {
            modelData.textures = loadTextures(json.getAsJsonObject("textures"), parent != null && parent.textures != null ? parent.textures : Map.of());
        }
        JsonArray elements = parent != null ? parent.rawElements : json.getAsJsonArray("elements");
        modelData.elements = loadElements(elements);
        modelData.rawElements = elements;
        return modelData;
    }

    private boolean[] loadFullFace(JsonObject json) {
        boolean[] fullFaces = new boolean[6];
        if (json == null) {
            Arrays.fill(fullFaces, true);
            return fullFaces;
        }

        for (JsonElement jsonElement : json.getAsJsonArray()) {
            fullFaces[Direction.valueOf(jsonElement.getAsString().toUpperCase()).index] = true;
        }
        return fullFaces;
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
                Direction direction = Direction.valueOf(entry.getKey().toUpperCase());
                faces[direction.index] = loadFace(entry.getValue().getAsJsonObject(), direction);
            }
        }
        return faces;
    }

    private ModelData.Element.Cube.Face loadFace(JsonObject json, Direction direction) {
        ModelData.Element.Cube.Face face = new ModelData.Element.Cube.Face();
        face.texture = json.get("texture").getAsString();
        face.uv = loadVector4f(json.getAsJsonArray("uv"));
        face.cullFaces = loadCullFaces(json.get("cullFaces"), direction);
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

    private byte loadCullFaces(JsonElement json, Direction defaultCullFace) {
        byte cullFaces = 0;
        if (json != null) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                cullFaces |= 1 << Direction.valueOf(jsonElement.getAsString().toUpperCase()).index;
            }
        } else {
            cullFaces |= 1 << defaultCullFace.index;
        }
        return cullFaces;
    }
}

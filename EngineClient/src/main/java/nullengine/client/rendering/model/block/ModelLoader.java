package nullengine.client.rendering.model.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nullengine.client.asset.AssetType;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.exception.AssetNotFoundException;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.model.BakedModel;
import nullengine.util.Direction;
import nullengine.util.JsonUtils;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

class ModelLoader {

    private static final Vector4f DEFAULT_UV = new Vector4f(0, 0, 1, 1);

    private final BlockModelManager modelManager;
    private final AssetSourceManager sourceManager;
    private final AssetType<BakedModel> type;

    public ModelLoader(BlockModelManager modelManager, AssetSourceManager sourceManager, AssetType<BakedModel> type) {
        this.modelManager = modelManager;
        this.sourceManager = sourceManager;
        this.type = type;
    }

    public ModelData load(AssetURL url) {
        var path = sourceManager.getPath(url.toFileLocation(type));
        if (path.isEmpty())
            throw new AssetNotFoundException(url);
        try {
            try (var reader = Files.newBufferedReader(path.get())) {
                return load(url, JsonUtils.DEFAULT_JSON_PARSER.parse(reader).getAsJsonObject());
            }
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model.", e);
        }
    }

    private ModelData load(AssetURL url, JsonObject json) {
        var modelData = new ModelData();
        modelData.url = url;
        modelData.fullFaces = loadFullFace(json.getAsJsonObject("fullFaces"));
        ModelData parent = null;
        if (json.has("parent")) {
            parent = modelManager.getModelData(AssetURL.fromString(url, json.get("parent").getAsString()));
        }
        modelData.textures = loadTextures(json.getAsJsonObject("textures"), parent != null ? parent.textures : Map.of());
        modelData.cubes = loadCubes(json.getAsJsonArray("cubes"), parent);
        return modelData;
    }

    private boolean[] loadFullFace(JsonObject json) {
        var fullFaces = new boolean[6];
        if (json == null) {
            Arrays.fill(fullFaces, true);
            return fullFaces;
        }

        for (var jsonElement : json.getAsJsonArray()) {
            fullFaces[Direction.valueOf(jsonElement.getAsString().toUpperCase()).index] = true;
        }
        return fullFaces;
    }

    private Map<String, String> loadTextures(JsonObject json, Map<String, String> parent) {
        if (json == null) {
            return Map.copyOf(parent);
        }

        var map = new HashMap<>(parent);
        for (var entry : json.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }

        for (var key : map.keySet()) {
            map.computeIfPresent(key, (k, v) -> resolveTexture(v, map));
        }

        return Map.copyOf(map);
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        if (texture.charAt(0) == '$') {
            texture = textures.getOrDefault(texture.substring(1), texture);
        }
        return texture;
    }

    private ModelData.Cube[] loadCubes(JsonArray json, ModelData parent) {
        List<ModelData.Cube> cubes = new ArrayList<>();
        if (parent != null) {
            for (var cube : parent.cubes) {
                cubes.add(cube.clone());
            }
        }
        if (json != null) {
            for (var jsonElement : json) {
                cubes.add(loadCube(jsonElement.getAsJsonObject()));
            }
        }
        return cubes.toArray(new ModelData.Cube[0]);
    }

    private ModelData.Cube loadCube(JsonObject json) {
        var cube = new ModelData.Cube();
        var from = loadVector3f(json.getAsJsonArray("from"));
        var to = loadVector3f(json.getAsJsonArray("to"));
        checkMinAndMax(from, to);

        cube.from = from;
        cube.to = to;
        cube.faces = loadFaces(json.getAsJsonObject("faces"));
        return cube;
    }

    private ModelData.Cube.Face[] loadFaces(JsonObject json) {
        var faces = new ModelData.Cube.Face[6];
        if (json != null) {
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                Direction direction = Direction.valueOf(entry.getKey().toUpperCase());
                faces[direction.index] = loadFace(entry.getValue().getAsJsonObject(), direction);
            }
        }
        return faces;
    }

    private ModelData.Cube.Face loadFace(JsonObject json, Direction direction) {
        var face = new ModelData.Cube.Face();
        face.texture = json.get("texture").getAsString();
        face.uv = loadVector4f(json.getAsJsonArray("uv"));
        face.cullFaces = loadCullFaces(json.get("cullFaces"), direction);
        return face;
    }

    private Vector3f loadVector3f(JsonArray json) {
        return new Vector3f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat());
    }

    private Vector4f loadVector4f(JsonArray json) {
        return json == null ? DEFAULT_UV :
                new Vector4f(json.get(0).getAsFloat(), json.get(1).getAsFloat(), json.get(2).getAsFloat(), json.get(3).getAsFloat());
    }

    private void checkMinAndMax(Vector3f min, Vector3f max) {
        float t;
        if (min.x > max.x) {
            t = min.x;
            min.x = max.x;
            max.x = t;
        }
        if (min.y > max.y) {
            t = min.y;
            min.y = max.y;
            max.y = t;
        }
        if (min.z > max.z) {
            t = min.z;
            min.z = max.z;
            max.z = t;
        }
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

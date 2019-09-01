package nullengine.client.rendering.model;

import com.google.gson.*;
import nullengine.client.asset.AssetType;
import nullengine.client.asset.AssetURL;
import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.source.AssetSourceManager;
import nullengine.client.rendering.model.data.Cube;
import nullengine.client.rendering.model.data.ModelData;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class ModelLoader {

    private final ModelManager modelManager;

    public ModelLoader(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vector3f.class, new JsonDeserializer<Vector3f>() {
                @Override
                public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    var array = json.getAsJsonArray();
                    return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
                }
            })
            .registerTypeAdapter(Vector4f.class, new JsonDeserializer<Vector4f>() {
                @Override
                public Vector4f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    var array = json.getAsJsonArray();
                    return new Vector4f(array.get(0).getAsFloat(), array.get(1).getAsFloat(),
                            array.get(2).getAsFloat(), array.get(3).getAsFloat());
                }
            })
            .registerTypeAdapter(Cube.class, Cube.Deserializer.INSTANCE)
            .registerTypeAdapter(ModelData.class, ModelData.Deserializer.INSTANCE)
            .create();

    public ModelData load(AssetSourceManager sourceManager, AssetType<?> type, AssetURL url) {
        return load(url, sourceManager.getPath(url.toFileLocation(type))
                .orElseThrow(() -> new AssetLoadException("Asset is not found. URL: " + url)));
    }

    private ModelData load(AssetURL url, Path path) {
        try (var reader = Files.newBufferedReader(path)) {
            var modelData = GSON.fromJson(reader, ModelData.class);
            modelData.url = url;
            resolveParent(modelData);
            resolveTextures(modelData);
            return modelData;
        } catch (IOException e) {
            throw new AssetLoadException("Cannot load model. URL: " + url, e);
        }
    }

    private void resolveParent(ModelData modelData) {
        if (isNullOrEmpty(modelData.parent)) {
            return;
        }

        var parentUrl = AssetURL.fromString(modelData.url, modelData.parent);
        modelData.parentInstance = modelManager.getModelData(parentUrl);
    }

    private void resolveTextures(ModelData modelData) {
        var parent = modelData.parentInstance;
        if (parent == null) {
            return;
        }
    }

    private String resolveTexture(String texture, Map<String, String> textures) {
        if (texture.charAt(0) == '$') {
            texture = textures.getOrDefault(texture.substring(1), texture);
        }
        return texture;
    }
}

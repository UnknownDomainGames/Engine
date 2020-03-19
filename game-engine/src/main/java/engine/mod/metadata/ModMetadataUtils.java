package engine.mod.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import engine.mod.Dependency;
import engine.mod.InstallationType;
import engine.mod.ModMetadata;
import engine.util.versioning.Version;

public final class ModMetadataUtils {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Version.class, new VersionPersistence())
            .registerTypeAdapter(InstallationType.class, new InstallationTypePersistence())
            .registerTypeAdapter(Dependency.class, new DependencyPersistence())
            .create();

    public static JsonObject toJson(ModMetadata descriptor) {
        return GSON.toJsonTree(descriptor).getAsJsonObject();
    }

    public static ModMetadata fromJson(JsonObject jsonObject) {
        return GSON.fromJson(jsonObject, ModMetadata.class);
    }

    private ModMetadataUtils() {
    }
}

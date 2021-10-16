package engine.mod.metadata;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import engine.mod.Dependency;
import engine.mod.InstallationType;
import engine.mod.ModMetadata;
import engine.util.versioning.Version;

public final class ModMetadataUtils {

    public static final String METADATA_NAME = "metadata.json";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Version.class, new VersionPersistence())
            .registerTypeAdapter(InstallationType.class, new InstallationTypePersistence())
            .registerTypeAdapter(Dependency.class, new DependencyPersistence())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public static void toJson(ModMetadata metadata, Appendable writer) {
        GSON.toJson(metadata, writer);
    }

    public static ModMetadata fromJson(JsonObject jsonObject) {
        return GSON.fromJson(jsonObject, ModMetadata.class);
    }

    private ModMetadataUtils() {
    }
}

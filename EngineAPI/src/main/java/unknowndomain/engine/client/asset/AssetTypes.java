package unknowndomain.engine.client.asset;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.model.voxel.VoxelModel;
import unknowndomain.engine.exception.NotInitializationException;

import java.util.Optional;

public final class AssetTypes {

    public static final AssetType<VoxelModel> VOXEL_MODEL = getType("VoxelModel");

    private static <T> AssetType<T> getType(String name) {
        Optional<AssetType<?>> type = Platform.getEngineClient().getAssetManager().getType(name);
        if (type.isEmpty()) {
            throw new NotInitializationException("Not initialize type \"" + name + "\".");
        }
        return (AssetType<T>) type.get();
    }

    private AssetTypes() {
        throw new AssertionError();
    }
}

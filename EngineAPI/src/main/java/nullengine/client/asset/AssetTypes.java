package nullengine.client.asset;

import nullengine.Platform;
import nullengine.client.asset.model.voxel.VoxelModel;
import nullengine.exception.NoInitializationException;

import java.util.Optional;

public final class AssetTypes {

    public static final AssetType<VoxelModel> VOXEL_MODEL = getType("VoxelModel");

    private static <T> AssetType<T> getType(String name) {
        Optional<AssetType<?>> type = Platform.getEngineClient().getAssetManager().getType(name);
        if (type.isEmpty()) {
            throw new NoInitializationException("Not initialize type \"" + name + "\".");
        }
        return (AssetType<T>) type.get();
    }

    private AssetTypes() {
        throw new AssertionError();
    }
}

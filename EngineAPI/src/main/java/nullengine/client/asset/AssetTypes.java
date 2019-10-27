package nullengine.client.asset;

import nullengine.Platform;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.model.BakedModel;
import nullengine.exception.UninitializationException;

import java.util.Optional;

public final class AssetTypes {

    public static final AssetType<GLTexture2D> TEXTURE = getType("Texture");
    public static final AssetType<BakedModel> VOXEL_MODEL = getType("VoxelModel");

    private static <T> AssetType<T> getType(String name) {
        Optional<AssetType<?>> type = Platform.getEngineClient().getAssetManager().getType(name);
        if (type.isEmpty()) {
            throw new UninitializationException("Not initialize type \"" + name + "\".");
        }
        return (AssetType<T>) type.get();
    }

    private AssetTypes() {
        throw new AssertionError();
    }
}

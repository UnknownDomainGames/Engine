package engine.client.asset;

import engine.Platform;
import engine.client.sound.Sound;
import engine.graphics.model.BakedModel;
import engine.graphics.texture.Texture2D;

import java.util.Optional;

public final class AssetTypes {

    public static final AssetType<Texture2D> TEXTURE = getType("Texture");
    public static final AssetType<BakedModel> VOXEL_MODEL = getType("VoxelModel");
    public static final AssetType<Sound> SOUND = getType("Sound");

    private static <T> AssetType<T> getType(String name) {
        Optional<AssetType<?>> type = Platform.getEngineClient().getAssetManager().getType(name);
        if (type.isEmpty()) {
            throw new IllegalStateException("Not initialize type \"" + name + "\".");
        }
        return (AssetType<T>) type.get();
    }

    private AssetTypes() {
        throw new AssertionError();
    }
}

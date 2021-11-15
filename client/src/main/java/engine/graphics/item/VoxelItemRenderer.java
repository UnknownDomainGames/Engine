package engine.graphics.item;

import engine.Platform;
import engine.client.asset.Asset;
import engine.client.asset.AssetTypes;
import engine.client.asset.AssetURL;
import engine.graphics.model.BakedModel;
import engine.graphics.vertex.VertexDataBuffer;
import engine.item.ItemStack;

public class VoxelItemRenderer implements ItemRenderer {

    private Asset<BakedModel> model;

    public VoxelItemRenderer(AssetURL url) {
        this.model = Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, url);
    }

    @Override
    public void init() {
    }

    @Override
    public void generateMesh(VertexDataBuffer buffer, ItemStack itemStack, float partial) {
        model.get().putVertexes(buffer, 0);
    }

    @Override
    public void dispose() {

    }
}

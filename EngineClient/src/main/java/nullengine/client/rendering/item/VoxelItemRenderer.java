package nullengine.client.rendering.item;

import nullengine.Platform;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetTypes;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.model.BakedModel;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.item.ItemStack;

public class VoxelItemRenderer implements ItemRenderer {

    private Asset<BakedModel> model;

    public VoxelItemRenderer(AssetURL url) {
        this.model = Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, url);
    }

    @Override
    public void init() {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        DirectRenderer directRenderer = DirectRenderer.getInstance();
        GLBuffer buffer = directRenderer.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
        model.get().putVertexes(buffer, 0);
        directRenderer.draw(DrawMode.TRIANGLES);
    }

    @Override
    public void dispose() {

    }
}

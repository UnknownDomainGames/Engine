package engine.graphics.item;

import engine.Platform;
import engine.client.asset.Asset;
import engine.client.asset.AssetTypes;
import engine.client.asset.AssetURL;
import engine.graphics.gl.GLStreamedRenderer;
import engine.graphics.model.BakedModel;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
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
    public void render(ItemStack itemStack, float partial) {
        GLStreamedRenderer renderer = GLStreamedRenderer.getInstance();
        VertexDataBuf buffer = renderer.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
        model.get().putVertexes(buffer, 0);
        renderer.draw(DrawMode.TRIANGLES);
    }

    @Override
    public void dispose() {

    }
}

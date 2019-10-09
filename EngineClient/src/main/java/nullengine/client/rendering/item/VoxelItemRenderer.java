package nullengine.client.rendering.item;

import nullengine.Platform;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetTypes;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.gl.GLDrawMode;
import nullengine.client.rendering.gl.buffer.GLBuffer;
import nullengine.client.rendering.gl.buffer.GLBufferFormats;
import nullengine.client.rendering.model.BakedModel;
import nullengine.item.ItemStack;

public class VoxelItemRenderer implements ItemRenderer {

    private Asset<BakedModel> model;

    public VoxelItemRenderer(AssetURL url) {
        this.model = Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, url);
    }

    @Override
    public void init(RenderManager context) {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLDrawMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
        model.get().putVertexes(buffer, 0);
        tessellator.draw();
    }

    @Override
    public void dispose() {

    }
}

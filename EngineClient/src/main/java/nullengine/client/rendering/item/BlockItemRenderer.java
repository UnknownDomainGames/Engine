package nullengine.client.rendering.item;

import nullengine.block.Block;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.GLDrawMode;
import nullengine.client.rendering.gl.GLVertexFormats;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;

public class BlockItemRenderer implements ItemRenderer {

    @Override
    public void init(RenderManager context) {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLDrawMode.TRIANGLES, GLVertexFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
        BlockRenderManager.instance().generateMesh(block, buffer);
        tessellator.draw();
    }

    @Override
    public void dispose() {

    }
}

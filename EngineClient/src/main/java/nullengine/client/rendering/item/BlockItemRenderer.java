package nullengine.client.rendering.item;

import nullengine.block.Block;
import nullengine.client.rendering.block.BlockRenderManager;
import nullengine.client.rendering.gl.DirectRenderer;
import nullengine.client.rendering.util.DrawMode;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.client.rendering.vertex.VertexFormat;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;

public class BlockItemRenderer implements ItemRenderer {

    @Override
    public void init() {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        DirectRenderer directRenderer = DirectRenderer.getInstance();
        VertexDataBuf buffer = directRenderer.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
        BlockRenderManager.instance().generateMesh(block, buffer);
        directRenderer.draw(DrawMode.TRIANGLES);
    }

    @Override
    public void dispose() {

    }
}

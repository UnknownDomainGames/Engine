package engine.graphics.item;

import engine.block.Block;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.gl.GLStreamedRenderer;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.item.BlockItem;
import engine.item.ItemStack;

public class BlockItemRenderer implements ItemRenderer {

    @Override
    public void init() {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        GLStreamedRenderer renderer = GLStreamedRenderer.getInstance();
        VertexDataBuf buffer = renderer.getBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
        BlockRenderManager.instance().generateMesh(block, buffer);
        renderer.draw(DrawMode.TRIANGLES);
    }

    @Override
    public void dispose() {

    }
}

package engine.graphics.item;

import engine.block.Block;
import engine.graphics.block.BlockRenderManager;
import engine.graphics.vertex.VertexDataBuf;
import engine.item.BlockItem;
import engine.item.ItemStack;

public class BlockItemRenderer implements ItemRenderer {

    @Override
    public void init() {
    }

    @Override
    public void generateMesh(VertexDataBuf buffer, ItemStack itemStack, float partial) {
        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        BlockRenderManager.instance().generateMesh(block, buffer);
    }

    @Override
    public void dispose() {

    }
}

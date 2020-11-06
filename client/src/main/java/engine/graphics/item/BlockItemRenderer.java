package engine.graphics.item;

import engine.block.state.BlockState;
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
        BlockState block = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
        BlockRenderManager.instance().generateMesh(block, buffer);
    }

    @Override
    public void dispose() {

    }
}

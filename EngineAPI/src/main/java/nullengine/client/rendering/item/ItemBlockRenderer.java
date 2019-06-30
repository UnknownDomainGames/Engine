package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.block.BlockRenderer;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;

public class ItemBlockRenderer implements ItemRenderer {

    @Override
    public void init(RenderContext context) {
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        var block = ((BlockItem) itemStack.getItem()).getBlock();
        block.getComponent(BlockRenderer.class).ifPresent(renderer -> {
            Tessellator tessellator = Tessellator.getInstance();
            GLBuffer buffer = tessellator.getBuffer();
            buffer.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
            if (renderer.isVisible()) {
                renderer.generateMesh(block, buffer);
            }
            tessellator.draw();
        });
    }

    @Override
    public void dispose() {

    }
}

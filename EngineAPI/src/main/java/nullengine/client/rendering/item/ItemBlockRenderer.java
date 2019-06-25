package nullengine.client.rendering.item;

import nullengine.client.block.ClientBlock;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;
import nullengine.registry.Registry;

public class ItemBlockRenderer implements ItemRenderer {

    private Registry<ClientBlock> clientBlockRegistry;

    @Override
    public void init(RenderContext context) {
        clientBlockRegistry = context.getEngine().getCurrentGame().getRegistryManager().getRegistry(ClientBlock.class);
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        ClientBlock clientBlock = clientBlockRegistry.getValue(((BlockItem) itemStack.getItem()).getBlock().getId());

        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();
        buffer.begin(GLBufferMode.TRIANGLES, GLBufferFormats.POSITION_COLOR_ALPHA_TEXTURE_NORMAL);
        clientBlock.getRenderer().generate(clientBlock, buffer);
        tessellator.draw();
    }

    @Override
    public void dispose() {

    }
}

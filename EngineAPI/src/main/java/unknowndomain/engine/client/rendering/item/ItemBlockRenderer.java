package unknowndomain.engine.client.rendering.item;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.registry.Registry;

public class ItemBlockRenderer implements ItemRenderer {

    private Registry<ClientBlock> clientBlockRegistry;

    @Override
    public void init(RenderContext context) {
        clientBlockRegistry = context.getEngine().getCurrentGame().getRegistryManager().getRegistry(ClientBlock.class);
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        ClientBlock clientBlock = clientBlockRegistry.getValue(((ItemBlock) itemStack.getItem()).getBlock().getId());

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

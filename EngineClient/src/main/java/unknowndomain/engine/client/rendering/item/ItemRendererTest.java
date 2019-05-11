package unknowndomain.engine.client.rendering.item;

import org.joml.Matrix4f;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;
import unknowndomain.engine.entity.component.TwoHands;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.registry.Registry;

import java.util.Optional;

public class ItemRendererTest implements ItemRenderer {

    private RenderContext context;
    private GameClient game;

    private Registry<ClientBlock> clientBlockRegistry;

    public void init(RenderContext context) {
        this.context = context;
        this.game = context.getEngine().getCurrentGame();
        clientBlockRegistry = game.getRegistryManager().getRegistry(ClientBlock.class);
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

    public void renderEntity(ItemStack itemStack, float partialTick) {
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f().translate(0, 4.5f - .5f * (float) Math.sin(Math.toRadians((game.getWorld().getGameTick() + partialTick) * 10)), 0)
                .scale(1f / 3, 1f / 3, 1f / 3)
                .rotateY(((int) System.currentTimeMillis() % 360000) / 1000f)
                .translate(-.5f, 0, -.5f));

        render(itemStack, partialTick);
    }

    @Deprecated
    public void render(float partial) {
        Optional<TwoHands> twoHands = game.getPlayer().getControlledEntity().getComponent(TwoHands.class);
        if (twoHands.isEmpty())
            return;

        renderEntity(twoHands.get().getMainHand(), partial);
    }

    public void dispose() {
//        ShaderManager.INSTANCE.unregisterShader("item");
    }
}

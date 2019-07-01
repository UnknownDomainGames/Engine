package nullengine.client.rendering.item;

import nullengine.client.game.GameClient;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.block.BlockRenderer;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.entity.component.TwoHands;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;
import org.joml.Matrix4f;

import java.util.Optional;

public class ItemRendererTest implements ItemRenderer {

    private RenderContext context;
    private GameClient game;

    public void init(RenderContext context) {
        this.context = context;
        this.game = context.getEngine().getCurrentGame();
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

    public void renderEntity(ItemStack itemStack, float partialTick) {
        ShaderManager.setUniform("u_ModelMatrix", new Matrix4f().translate(0, 4.5f - .5f * (float) Math.sin(Math.toRadians((game.getWorld().getGameTick() + partialTick) * 10)), 0)
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

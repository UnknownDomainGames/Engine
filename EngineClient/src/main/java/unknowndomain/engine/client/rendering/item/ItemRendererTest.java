package unknowndomain.engine.client.rendering.item;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.game.GameClient;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.entity.component.TwoHands;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.registry.Registry;

import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;
import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public class ItemRendererTest implements ItemRenderer {

    private RenderContext context;
    private GameClient game;
    //    private ObservableValue<ShaderProgram> itemShader;

    private Registry<ClientBlock> clientBlockRegistry;

    public void init(RenderContext context) {
        this.context = context;
        this.game = context.getEngine().getCurrentGame();
        clientBlockRegistry = game.getRegistryManager().getRegistry(ClientBlock.class);
//        itemShader = ShaderManager.INSTANCE.registerShader("item", new ShaderProgramBuilder()
//                .addShader(VERTEX_SHADER, AssetPath.of("engine", "shader", "item.vert"))
//                .addShader(FRAGMENT_SHADER, AssetPath.of("engine", "shader", "item.frag")));
    }

    private void preRender() {
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
//        glFrontFace(GL_CCW);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        context.getTextureManager().getTextureAtlas(BLOCK).getValue().bind();
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        preRender();

        ClientBlock clientBlock = clientBlockRegistry.getValue(((ItemBlock) itemStack.getItem()).getBlock().getId());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_TRIANGLES, true, true, true, true);
        clientBlock.getRenderer().generate(clientBlock, buffer);
        tessellator.draw();

        postRender();
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

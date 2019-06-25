package nullengine.client.rendering.item;

import nullengine.client.event.rendering.ItemRendererRegistrationEvent;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ItemRenderManagerImpl implements ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private final ItemRendererTest defaultItemRenderer = new ItemRendererTest();

    private RenderContext context;

    @Override
    public void register(Item item, ItemRenderer renderer) {
        if (itemRendererMap.containsKey(item))
            throw new IllegalArgumentException();

        itemRendererMap.put(item, renderer);
    }

    public void init(RenderContext context) {
        this.context = context;
        defaultItemRenderer.init(context);

        context.getEngine().getCurrentGame().getEventBus().post(new ItemRendererRegistrationEvent(this));

        itemRendererMap.values().forEach(itemRenderer -> itemRenderer.init(context));
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        if (itemStack.isEmpty())
            return;

        preRender();

        itemRendererMap.getOrDefault(itemStack.getItem(), defaultItemRenderer).render(itemStack, partial);

        postRender();
    }

    private void preRender() {
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL11.GL_CULL_FACE);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);

        context.getTextureManager().getTextureAtlas(StandardTextureAtlas.BLOCK).getTexture().getValue().bind();
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL11.GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_DEPTH_TEST);
        glDisable(GL11.GL_BLEND);
    }

    public void dispose() {
        defaultItemRenderer.dispose();

        itemRendererMap.values().forEach(ItemRenderer::dispose);
    }
}

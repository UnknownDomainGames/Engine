package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.texture.StandardTextureAtlas;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import nullengine.registry.Registries;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class ItemRenderManagerImpl implements ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private RenderManager context;

    private void register(Item item, ItemRenderer renderer) {
        if (itemRendererMap.containsKey(item))
            throw new IllegalArgumentException();

        itemRendererMap.put(item, renderer);
    }

    public void init(RenderManager context) {
        this.context = context;
        Registries.getItemRegistry().getValues().forEach(item ->
                item.getComponent(ItemRenderer.class)
                        .ifPresent(itemRenderer -> register(item, itemRenderer)));
        itemRendererMap.values().forEach(itemRenderer -> itemRenderer.init(context));
    }

    @Override
    public void render(ItemStack itemStack, float partial) {
        if (itemStack.isEmpty())
            return;

        var render = itemRendererMap.get(itemStack.getItem());
        if (render == null) {
            return;
        }

        preRender();

        render.render(itemStack, partial);

        postRender();
    }

    private void preRender() {
        context.getTextureManager().getTextureAtlas(StandardTextureAtlas.DEFAULT).bind();
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        itemRendererMap.values().forEach(ItemRenderer::dispose);
    }
}

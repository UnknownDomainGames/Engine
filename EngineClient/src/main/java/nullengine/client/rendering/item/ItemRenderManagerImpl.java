package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderManager;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import nullengine.registry.Registries;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public final class ItemRenderManagerImpl implements ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private final ItemRenderer blockItemRenderer = new BlockItemRenderer();

    private RenderManager manager;

    public void init(RenderManager manager) {
        this.manager = manager;
        Registries.getItemRegistry().getValues().forEach(this::registerItemRenderer);
        itemRendererMap.values().forEach(ItemRenderer::init);
    }

    private void registerItemRenderer(Item item) {
        if (item instanceof BlockItem) {
            itemRendererMap.putIfAbsent(item, blockItemRenderer);
            return;
        }

        item.getComponent(ItemDisplay.class).ifPresent(itemDisplay ->
                itemRendererMap.putIfAbsent(item, new VoxelItemRenderer(itemDisplay.getModelUrl())));
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
        manager.getTextureManager().getDefaultAtlas().bind();
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        itemRendererMap.values().forEach(ItemRenderer::dispose);
    }
}

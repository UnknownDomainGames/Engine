package engine.graphics.item;

import engine.graphics.voxel.VoxelRenderHelper;
import engine.item.BlockItem;
import engine.item.Item;
import engine.item.ItemStack;
import engine.registry.Registries;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public final class ItemRenderManagerImpl implements ItemRenderManager {

    private final Map<Item, ItemRenderer> itemRendererMap = new HashMap<>();

    private final ItemRenderer blockItemRenderer = new BlockItemRenderer();

    public void init() {
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
        VoxelRenderHelper.getVoxelTextureAtlas().getTexture().bind();
    }

    private void postRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        itemRendererMap.values().forEach(ItemRenderer::dispose);
    }
}

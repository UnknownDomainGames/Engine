package engine.graphics.item;

import engine.graphics.graph.Renderer;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.item.BlockItem;
import engine.item.Item;
import engine.item.ItemStack;
import engine.registry.Registries;

import java.util.HashMap;
import java.util.Map;

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
    public void render(Renderer renderer, ItemStack itemStack, float partial) {
        if (itemStack.isEmpty())
            return;

        VertexDataBuf buffer = VertexDataBuf.currentThreadBuffer();
        buffer.begin(VertexFormat.POSITION_COLOR_ALPHA_TEX_COORD_NORMAL);
        generateMesh(buffer, itemStack, partial);
        buffer.finish();
        renderer.drawStreamed(DrawMode.TRIANGLES, buffer);
    }

    @Override
    public void generateMesh(VertexDataBuf buffer, ItemStack itemStack, float partial) {
        var render = itemRendererMap.get(itemStack.getItem());
        if (render == null) return;
        render.generateMesh(buffer, itemStack, partial);
    }

//    private void preRender() {
//        VoxelGraphicsHelper.getVoxelTextureAtlas().getTexture().bind();
//    }

    public void dispose() {
        itemRendererMap.values().forEach(ItemRenderer::dispose);
    }
}

package engine.graphics.item;

import engine.graphics.graph.Renderer;
import engine.graphics.vertex.VertexDataBuffer;
import engine.item.ItemStack;

import java.util.function.Supplier;

public interface ItemRenderManager {
    static ItemRenderManager instance() {
        return ItemRenderManager.Internal.instance.get();
    }

    void render(Renderer renderer, ItemStack itemStack, float partial);

    void generateMesh(VertexDataBuffer buffer, ItemStack itemStack, float partial);

    class Internal {
        private static Supplier<ItemRenderManager> instance = () -> {
            throw new IllegalStateException("ItemRenderManager is uninitialized");
        };

        public static void setInstance(ItemRenderManager instance) {
            ItemRenderManager.Internal.instance = () -> instance;
        }
    }
}

package nullengine.client.rendering.entity;

import nullengine.entity.Entity;
import nullengine.exception.UninitializationException;

import java.util.function.Supplier;

public interface EntityRenderManager {
    void render(Entity entity, float partial);

    static EntityRenderManager instance() {
        return EntityRenderManager.Internal.instance.get();
    }

    class Internal {
        private static Supplier<EntityRenderManager> instance = UninitializationException.supplier("EntityRenderManager is uninitialized");

        public static void setInstance(EntityRenderManager instance) {
            EntityRenderManager.Internal.instance = () -> instance;
        }
    }
}

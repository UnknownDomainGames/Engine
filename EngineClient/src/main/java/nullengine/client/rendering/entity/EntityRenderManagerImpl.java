package nullengine.client.rendering.entity;

import nullengine.client.event.rendering.RegisterEntityRendererEvent;
import nullengine.client.rendering.RenderManager;
import nullengine.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityRenderManagerImpl implements EntityRenderManager {

    private final Map<Class<? extends Entity>, EntityRenderer<?>> renderers = new HashMap<>();

    public void init(RenderManager context) {
        EntityRenderManagerImpl.Internal.setInstance(this);
        context.getEngine().getCurrentGame().getEventBus().post(new RegisterEntityRendererEvent(this::register));
        renderers.values().forEach(entityRenderer -> entityRenderer.init(context));
    }

    private <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        if (renderers.containsKey(entityType))
            throw new IllegalArgumentException();

        renderers.put(entityType, renderer);
    }

    @Override
    public void render(Entity entity, float partial) {
        EntityRenderer renderer = renderers.get(entity.getClass());
        if (renderer == null)
            return;

        var position = entity.getPosition();
        var motion = entity.getMotion();
        var x = position.x() + motion.x() * partial;
        var y = position.y() + motion.y() * partial;
        var z = position.z() + motion.z() * partial;

        if (!renderer.shouldRender(entity, x, y, z, partial))
            return;

        renderer.render(entity, x, y, z, partial);
    }

    public void dispose() {
        renderers.forEach((key, value) -> value.dispose());
    }
}

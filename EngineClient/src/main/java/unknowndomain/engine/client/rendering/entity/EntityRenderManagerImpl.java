package unknowndomain.engine.client.rendering.entity;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityRenderManagerImpl implements EntityRenderManager {

    private final Map<Class<? extends Entity>, EntityRenderer<?>> renderers = new HashMap<>();

    private RenderContext context;

    @Override
    public <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        if (renderers.containsKey(entityType))
            throw new IllegalArgumentException();

        renderers.put(entityType, renderer);
    }

    public void init(RenderContext context) {
        this.context = context;
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

        renderer.render(entity, x, y, z, partial);
    }

    public void dispose() {

    }
}

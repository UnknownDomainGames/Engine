package unknowndomain.engine.client.rendering.entity;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.component.Owner;
import unknowndomain.engine.entity.Entity;

@Owner(RenderContext.class)
public interface EntityRenderManager extends Component {
    <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer);

    void render(Entity entity, float partial);
}

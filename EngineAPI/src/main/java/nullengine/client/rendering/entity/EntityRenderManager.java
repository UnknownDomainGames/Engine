package nullengine.client.rendering.entity;

import nullengine.client.rendering.RenderContext;
import nullengine.component.Component;
import nullengine.component.Owner;
import nullengine.entity.Entity;

@Owner(RenderContext.class)
public interface EntityRenderManager extends Component {
    <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer);
}

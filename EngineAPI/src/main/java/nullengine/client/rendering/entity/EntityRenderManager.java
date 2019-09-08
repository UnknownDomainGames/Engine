package nullengine.client.rendering.entity;

import nullengine.client.rendering.RenderManager;
import nullengine.component.Component;
import nullengine.component.Owner;
import nullengine.entity.Entity;

@Owner(RenderManager.class)
public interface EntityRenderManager extends Component {
    <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer);
}

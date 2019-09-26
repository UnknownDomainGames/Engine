package nullengine.client.rendering.entity;

import nullengine.component.Component;
import nullengine.entity.Entity;

public interface EntityRenderManager extends Component {
    <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer);
}

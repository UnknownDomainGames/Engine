package unknowndomain.engine.event.registry;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.event.Event;

import java.util.Collections;
import java.util.List;

public class ClientRegistryEvent implements Event {
    private List<Renderer.Factory> renderers;

    public ClientRegistryEvent(List<Renderer.Factory> renderers) {
        this.renderers = renderers;
    }

    public List<Renderer.Factory> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    public ClientRegistryEvent registerRenderer(Renderer.Factory renderer) {
        renderers.add(Validate.notNull(renderer));
        return this;
    }
}

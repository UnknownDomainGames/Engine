package unknowndomain.engine.client.event.game;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.event.Event;

import java.util.Collections;
import java.util.List;

public class RendererRegisterEvent implements Event {
    private List<Renderer.Factory> renderers;

    public RendererRegisterEvent(List<Renderer.Factory> renderers) {
        this.renderers = renderers;
    }

    public List<Renderer.Factory> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    public RendererRegisterEvent registerRenderer(Renderer.Factory renderer) {
        renderers.add(Validate.notNull(renderer));
        return this;
    }
}

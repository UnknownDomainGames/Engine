package unknowndomain.engine.client.event.game;

import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.event.Event;

import java.util.List;

public class RendererRegisterEvent implements Event {
    private final List<Renderer> renderers;

    public RendererRegisterEvent(List<Renderer> renderers) {
        this.renderers = renderers;
    }

    public void register(Renderer renderer) {
        renderers.add(renderer);
    }
}

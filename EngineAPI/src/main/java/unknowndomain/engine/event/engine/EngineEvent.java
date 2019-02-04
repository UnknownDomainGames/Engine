package unknowndomain.engine.event.engine;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.event.Event;

import java.util.Collections;
import java.util.List;

/**
 * Events related to the Engine
 */
public class EngineEvent implements Event {
    /**
     * The Engine
     */
    private Engine engine;

    protected EngineEvent(Engine e) {
        engine = e;
    }

    public Engine getEngine() {
        return engine;
    }

    /* Fired when the Engine starts constructing resources */
    // TODO: Remove it.
    @Deprecated
    public static class ResourceConstructionStart extends EngineEvent {
        private final ResourceManager resourceManager;
        private final TextureManager textureManager;
        private final List<Renderer.Factory> renderers;

        public List<Renderer.Factory> getRenderers() {
            return Collections.unmodifiableList(renderers);
        }

        public ResourceConstructionStart(Engine e, ResourceManager resourceManager, TextureManager textureManager, List<Renderer.Factory> renderers) {
            super(e);
            this.resourceManager = resourceManager;
            this.textureManager = textureManager;
            this.renderers = renderers;
        }

        public ResourceManager getResourceManager() {
            return resourceManager;
        }

        public TextureManager getTextureManager() {
            return textureManager;
        }

        public void registerRenderer(Renderer.Factory renderer) {
            renderers.add(Validate.notNull(renderer));
        }
    }

    /* Fired when the Engine finishes constructing resources */
    @Deprecated
    public static class ResourceConstructionFinish extends EngineEvent {
        private final ResourceManager resourceManager;
        private final TextureManager textureManager;
        private final List<Renderer.Factory> renderers;

        public List<Renderer.Factory> getRenderers() {
            return Collections.unmodifiableList(renderers);
        }

        public ResourceConstructionFinish(Engine e, ResourceManager resourceManager, TextureManager textureManager, List<Renderer.Factory> renderers) {
            super(e);
            this.resourceManager = resourceManager;
            this.textureManager = textureManager;
            this.renderers = renderers;
        }

        public ResourceManager getResourceManager() {
            return resourceManager;
        }

        public TextureManager getTextureManager() {
            return textureManager;
        }

        public void registerRenderer(Renderer.Factory renderer) {
            renderers.add(Validate.notNull(renderer));
        }
    }

    /* Fired when the Engine finishes initializing and is ready to start games */
    public static class InitializationComplete extends EngineEvent {
        public InitializationComplete(Engine e) {
            super(e);
        }
    }
}

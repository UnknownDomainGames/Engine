package unknowndomain.engine.client.game;

import com.google.common.collect.Lists;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.RendererContext;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.GameCommon;

import java.util.List;

public class GameClient extends GameCommon {
    private RendererContext gameRenderer;
    private ResourceManager resourceManager;

    public GameClient(Config config, EventBus bus) {
        super(config, bus);
    }

    /**
     * @return the gameRenderer
     */
    public RendererContext getGameRenderer() {
        return gameRenderer;
    }

    @Override
    protected void registerStage() {
        super.registerStage();
        List<Renderer> renderers = Lists.newArrayList();
        // TODO: collect
        gameRenderer = new RendererContext(renderers, null);

    }

    @Override
    protected void resourceStage() {
        resourceManager = new ResourceManagerImpl();
    }

    //    public static GameClient create(Camera camera, List<Renderer> rendering) {
//
//    }

}
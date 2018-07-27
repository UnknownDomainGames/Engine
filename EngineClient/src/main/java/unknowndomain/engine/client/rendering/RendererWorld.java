package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.world.World;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;

public class RendererWorld implements Renderer {



    @Override
    public void render() {
        for(World world : UnknownDomain.getEngine().getGame().getWorlds()){

        }
    }
}

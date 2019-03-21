package unknowndomain.engine.client.game;

import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.registry.Registry;

@Deprecated
public interface ClientContext {

    RayTraceBlockHit getHit();

    Registry<ClientBlock> getClientBlockRegistry();
}

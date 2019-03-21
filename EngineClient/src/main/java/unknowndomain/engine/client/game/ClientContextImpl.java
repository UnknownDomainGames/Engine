package unknowndomain.engine.client.game;

import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.Registry;

public class ClientContextImpl implements ClientContext {

    private final GameClientStandalone game;

    private final Player player;

    private RayTraceBlockHit hit;

    public ClientContextImpl(GameClientStandalone game, Player player) {
        this.game = game;
        this.player = player;
    }


    @Override
    public RayTraceBlockHit getHit() {
        return hit;
    }

    @Override
    public Registry<ClientBlock> getClientBlockRegistry() {
        return game.getRegistryManager().getRegistry(ClientBlock.class);
    }
}

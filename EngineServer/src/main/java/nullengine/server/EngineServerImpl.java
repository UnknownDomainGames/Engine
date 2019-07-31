package nullengine.server;

import nullengine.EngineBase;
import nullengine.enginemod.EngineModListeners;
import nullengine.game.Game;
import nullengine.server.network.NetworkServer;
import nullengine.util.Side;

import java.nio.file.Path;

public class EngineServerImpl extends EngineBase implements EngineServer {
    private Thread serverThread;
    private NetworkServer nettyServer;
    public EngineServerImpl(Path runPath) {
        super(runPath);
    }

    @Override
    public Thread getServerThread() {
        return serverThread;
    }

    @Override
    public boolean isServerThread() {
        return Thread.currentThread() == serverThread;
    }

    @Override
    public Side getSide() {
        return Side.DEDICATED_SERVER;
    }

    @Override
    protected void constructionStage() {
        super.constructionStage();

        logger.info("Initializing server engine!");
        serverThread = Thread.currentThread();
        // TODO: Remove it
        modManager.getMod("engine").ifPresent(modContainer -> modContainer.getEventBus().register(EngineModListeners.class));
    }

    @Override
    public Game getCurrentGame() {
        return null;
    }

    @Override
    public void startGame(Game game) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }
}

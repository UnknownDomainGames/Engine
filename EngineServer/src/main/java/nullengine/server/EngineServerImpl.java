package nullengine.server;

import nullengine.EngineBase;
import nullengine.enginemod.EngineModListeners;
import nullengine.event.engine.EngineEvent;
import nullengine.game.Game;
import nullengine.logic.Ticker;
import nullengine.server.network.NetworkServer;
import nullengine.util.Side;

import java.nio.file.Path;

public class EngineServerImpl extends EngineBase implements EngineServer {
    private Thread serverThread;
    private Ticker ticker;
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
    protected void finishStage() {
        super.finishStage();

        ticker = new Ticker(this::serverTick, partial -> {}, 20);
    }

    @Override
    public void runEngine() {
        super.runEngine();

        addShutdownListener(ticker::stop);

        logger.info("Finishing initialization!");
        eventBus.post(new EngineEvent.Ready(this));
        nettyServer = new NetworkServer();
        var port = 1;
        logger.info("Starting server at {}:{}", "*", port);
        nettyServer.run(null, port);
        ticker.run();
    }

    @Override
    public Game getCurrentGame() {
        return null;
    }

    @Override
    public void startGame(Game game) {

    }

    public void serverTick(){
        nettyServer.tick();
        if (isMarkedTermination()) {
            if (isPlaying()) {
//                game.terminate();
            } else {
                tryTerminate();
            }
        }
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
//        if (isPlaying()) {
//            game.terminate();
//            game.clientTick();
//        }

        eventBus.post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
    }

    @Override
    public boolean isPlaying() {
        return false;
    }
}

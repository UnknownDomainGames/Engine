package nullengine.server;

import configuration.parser.ConfigParseException;
import nullengine.EngineBase;
import nullengine.enginemod.EngineModListeners;
import nullengine.enginemod.ServerHandlingListeners;
import nullengine.event.engine.EngineEvent;
import nullengine.game.Game;
import nullengine.game.GameServerFullAsync;
import nullengine.logic.Ticker;
import nullengine.server.network.NetworkServer;
import nullengine.util.Side;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Objects;

public class EngineServerImpl extends EngineBase implements EngineServer {
    private Thread serverThread;
    private Ticker ticker;
    private NetworkServer nettyServer;
    private ServerConfig serverConfig;
    private final Path configPath;
    private Game game;
    public EngineServerImpl(Path runPath, Path configPath) {
        super(runPath);
        this.configPath = configPath;
    }

    public EngineServerImpl(Path runPath, ServerConfig serverConfig){
        super(runPath);
        this.configPath = Path.of("");
        this.serverConfig = serverConfig;
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
        if (serverConfig == null || !configPath.equals(Path.of(""))) {
            try {
                serverConfig = new ServerConfig(configPath);
                serverConfig.load();
            }catch (ConfigParseException e){
                logger.warn("Cannot parse server config! Try creating new one", e);
                serverConfig = new ServerConfig();
                serverConfig.save();
            }
        }
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
        var ipStr = serverConfig.getServerIp();
        var port = serverConfig.getServerPort();
        getEventBus().register(ServerHandlingListeners.class);
        logger.info("Starting server at {}:{}", ipStr.isEmpty() ? "*" : ipStr, port);
        try {
            nettyServer.run(ipStr.isEmpty() ? null : InetAddress.getByName(ipStr), port);
        } catch (UnknownHostException e) {
            logger.warn(String.format("cannot start server at %s:%d", ipStr, port), e);
            terminate();
            return;
        }
        logger.info("Starting game for world");
        startGame(new GameServerFullAsync(this, this.getRunPath().resolve("game")));
        ticker.run();
    }

    @Override
    public Game getCurrentGame() {
        return game;
    }

    @Override
    public void startGame(Game game) {
        if(isPlaying()){
            throw new IllegalStateException("Game is running");
        }
        this.game = Objects.requireNonNull(game);
        game.init();
    }

    public void serverTick(){
        nettyServer.tick();
        if (isMarkedTermination()) {
            if (isPlaying()) {
                game.terminate();
            } else {
                tryTerminate();
            }
        }
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        if (isPlaying()) {
            game.terminate();
        }

        eventBus.post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        logger.info("Engine terminated!");
    }

    public NetworkServer getNettyServer() {
        return nettyServer;
    }

    @Override
    public boolean isPlaying() {
        return game != null && !game.isTerminated();
    }
}

package engine.server;

import configuration.io.ConfigLoadException;
import configuration.parser.ConfigParseException;
import engine.EngineBase;
import engine.Platform;
import engine.client.game.GameClient;
import engine.enginemod.EngineModListeners;
import engine.event.engine.EngineEvent;
import engine.game.Game;
import engine.game.GameDataStorage;
import engine.game.GameServerFullAsync;
import engine.logic.Ticker;
import engine.registry.Registries;
import engine.server.network.NetworkServer;
import engine.util.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Objects;

public class EngineServerImpl extends EngineBase implements EngineServer {
    private Thread serverThread;
    private Thread consoleReadingThread;
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
        return Side.SERVER;
    }

    @Override
    protected void constructionStage() {
        super.constructionStage();

        consoleReadingThread = new Thread("Console reader") {
            @Override
            public void run() {
                var in = new BufferedReader(new InputStreamReader(System.in));
                String input;
                try {
                    while (!EngineServerImpl.this.isMarkedTermination() && (input = in.readLine()) != null) {
                        if ("/stop".equals(input)) {
                            EngineServerImpl.this.terminate();
                        }
                    }
                } catch (IOException e) {
                    Platform.getLogger().warn("Cannot read console input!", e);
                }
            }
        };
        consoleReadingThread.setDaemon(true);
        consoleReadingThread.setUncaughtExceptionHandler((t, e) -> Platform.getLogger().error("Caught exception while reading console input!", e));
        consoleReadingThread.start();

        logger.info("Initializing server engine!");
        serverThread = Thread.currentThread();
        if (serverConfig == null || !configPath.equals(Path.of(""))) {
            try {
                serverConfig = new ServerConfig(configPath);
                serverConfig.load();
            } catch (ConfigParseException | ConfigLoadException e) {
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

        Registries.getBlockRegistry().reconstructStateId(); //TODO: any better place?

        ticker = new Ticker(this::serverTick, partial -> {
        }, 20);
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
        logger.info("Starting server at {}:{}", ipStr.isEmpty() ? "*" : ipStr, port);
        try {
            nettyServer.run(ipStr.isEmpty() ? null : InetAddress.getByName(ipStr), port);
        } catch (UnknownHostException e) {
            logger.warn(String.format("cannot start server at %s:%d", ipStr, port), e);
            terminate();
            return;
        }
        logger.info("Starting game for world");
        Path gameBasePath = this.getRunPath().resolve("games");
        var gameStorage = new GameDataStorage(gameBasePath);
        if (!gameStorage.getGames().containsKey(serverConfig.getGame())) {
            gameStorage.createGameData(serverConfig.getGame());
        }
        //TODO: chunkstatuslistener
        startGame(new GameServerFullAsync(this, gameBasePath, gameStorage.getGames().get(serverConfig.getGame()), nettyServer, () -> null));
        ticker.run();
    }

    @Override
    public Game getCurrentLogicGame() {
        return game;
    }

    @Override
    public GameClient getCurrentClientGame() {
        throw new UnsupportedOperationException("Cannot get client game from delegated server");
    }

    @Override
    public void startGame(Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }
        this.game = Objects.requireNonNull(game);
        game.init();
    }

    public void serverTick() {
        if (isMarkedTermination()) {
            if (isPlaying()) {
                game.terminate();
            } else {
                tryTerminate();
            }
        }
        nettyServer.tick();
        ((GameServerFullAsync) game).tick();
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
        nettyServer.close();
        if (isPlaying()) {
            game.terminate();
        }

        eventBus.post(new EngineEvent.PreTermination(this));

        ticker.stop();
        shutdownListeners.forEach(Runnable::run);
        consoleReadingThread.interrupt();
        logger.info("Engine terminated!");
    }

    public NetworkServer getNettyServer() {
        return nettyServer;
    }

    @Override
    public boolean isPlaying() {
        return game != null && game.isReadyToPlay() && !game.isTerminated();
    }
}

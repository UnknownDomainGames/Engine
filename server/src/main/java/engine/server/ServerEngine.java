package engine.server;

import configuration.parser.ConfigParseException;
import engine.BaseEngine;
import engine.Platform;
import engine.client.game.ClientGame;
import engine.enginemod.EngineModListeners;
import engine.event.engine.EngineEvent;
import engine.game.Game;
import engine.game.GameData;
import engine.game.ServerGame;
import engine.logic.Ticker;
import engine.server.network.NetworkServer;
import engine.util.Side;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Objects;

public class ServerEngine extends BaseEngine {
    private Thread serverThread;
    private Thread consoleReadingThread;
    private Ticker ticker;
    private NetworkServer nettyServer;
    private ServerConfig serverConfig;
    private final Path configPath;
    private Game game;

    public ServerEngine(Path runPath, Path configPath) {
        super(runPath);
        this.configPath = configPath;
    }

    public ServerEngine(Path runPath, ServerConfig serverConfig) {
        super(runPath);
        this.configPath = Path.of("");
        this.serverConfig = serverConfig;
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
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
    protected void constructionStage() {
        super.constructionStage();

        consoleReadingThread = new Thread("Console reader") {
            @Override
            public void run() {
                var in = new BufferedReader(new InputStreamReader(System.in));
                String input;
                try {
                    while (!ServerEngine.this.isMarkedTermination() && (input = in.readLine()) != null) {
                        if ("/stop".equals(input)) {
                            ServerEngine.this.terminate();
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
            } catch (ConfigParseException e) {
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

        ticker = new Ticker(this::serverTick, partial -> {
        }, 20);
    }

    @Override
    public void runStage() {
        super.runStage();

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
        Path gameBasePath = this.getRunPath().resolve("game");
        runServerGame(new ServerGame(this, gameBasePath, GameData.createFromCurrentEnvironment(gameBasePath, "default"), nettyServer));
        ticker.run();
    }

    @Override
    public Game getServerGame() {
        return game;
    }

    @Override
    public void runServerGame(@Nonnull Game game) {
        if (isPlaying()) {
            throw new IllegalStateException("Game is running");
        }
        this.game = Objects.requireNonNull(game);
        game.init();
    }

    @Override
    public Thread getClientThread() {
        return null;
    }

    @Override
    public boolean isClientThread() {
        return false;
    }

    @Override
    public ClientGame getClientGame() {
        throw new UnsupportedOperationException("client");
    }

    @Override
    public void runClientGame(@Nonnull ClientGame game) {
        throw new UnsupportedOperationException("client");
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
        game.update();
    }

    private void tryTerminate() {
        logger.info("Engine terminating!");
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
        return game != null && !game.isTerminated();
    }
}

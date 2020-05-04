package engine.game;

import engine.Engine;
import engine.event.EventBus;
import engine.event.game.GameCreateEvent;
import engine.event.game.GameStartEvent;
import engine.event.game.GameTerminationEvent;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public abstract class GameBase implements Game {

    protected final Engine engine;
    protected final Logger logger;

    protected final Path storagePath;

    protected final GameData data;

    protected EventBus eventBus;

    protected boolean markedTermination = false;
    protected boolean terminated = false;
    protected boolean isReady = false;

    public GameBase(Engine engine, Path storagePath, GameData data) {
        this.engine = engine;
        this.logger = engine.getLogger();
        this.eventBus = engine.getEventBus();
        this.storagePath = storagePath;
        this.data = data;
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
    }

    /**
     * let mod and resource related module load resources.
     */
    protected void resourceStage() {
    }

    /**
     * final stage of the
     */
    protected void finishStage() {
    }

    @Nonnull
    @Override
    public Engine getEngine() {
        return engine;
    }

    @Nonnull
    @Override
    public Path getStoragePath() {
        return storagePath;
    }

    @Nonnull
    @Override
    public GameData getData() {
        return data;
    }

    @Nonnull
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public boolean isMarkedTermination() {
        return markedTermination;
    }

    @Override
    public void init() {
        logger.info("Initializing Game.");

        doCreatePreInit();
        eventBus.post(new GameStartEvent.Pre(this));

        constructStage();
        resourceStage();
        finishStage();

        doCreatePostInit();
        eventBus.post(new GameStartEvent.Post(this));
        // TODO: loop to check if we need to gc the world

        // for (WorldCommon worldCommon : internalWorlds) {
        // worldCommon.stop();
        // }
    }

    protected void doCreatePreInit() {
        if (data.isCreated()) {
            return;
        }
        logger.info("First Initialize Game.");
        eventBus.post(new GameCreateEvent.Pre(this));
    }

    protected void doCreatePostInit() {
        if (data.isCreated()) {
            return;
        }
        data.setCreated();
        data.save();
        eventBus.post(new GameCreateEvent.Post(this));
    }

    @Override
    public synchronized void terminate() {
        markedTermination = true;
        logger.info("Marked game terminated!");
        eventBus.post(new GameTerminationEvent.Marked(this));
    }

    protected void markReady() {
        isReady = true;
    }

    @Override
    public boolean isReadyToPlay() {
        return isReady;
    }

    protected void tryTerminate() {
        terminated = true;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    // TODO: unload mod/resource
}

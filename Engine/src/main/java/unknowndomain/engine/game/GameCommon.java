package unknowndomain.engine.game;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.EntityType;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.ModRepository;
import unknowndomain.engine.mod.ModStore;
import unknowndomain.engine.mod.SimpleModManager;
import unknowndomain.engine.registry.FrozenRegistryManager;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GameCommon implements Game {
    protected final Option option;
    protected final EventBus bus;
    protected final ModRepository modRepository;
    protected final ModStore modStore;

    protected GameContext context;
    protected ModManager modManager;

    public GameCommon(Option option, ModRepository repository,
                      ModStore store, EventBus bus) {
        this.option = option;
        this.modStore = store;
        this.modRepository = repository;
        this.bus = bus;
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        modManager = SimpleModManager.load(
                modStore,
                modRepository,
                option.getMods());
        // TODO: collect mod from remote or local here
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        RegistryManager all = FrozenRegistryManager.collectAll(bus, Registry.Type.of("action", Action.class),
                Registry.Type.of("block", Block.class), Registry.Type.of("item", Item.class),
                Registry.Type.of("entity", EntityType.class));
        this.context = new GameContext(all, bus);
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
        spawnWorld(null);
        // TODO: post finish stage event to mods
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public RuntimeObject createObject(GameContext gameContext, Game context) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return null;
    }

    @Override
    public void run() {
        constructStage();
        registerStage();
        resourceStage();
        finishStage();

        // TODO: loop to check if we need to gc the world

        // for (WorldCommon worldCommon : internalWorlds) {
        // worldCommon.stop();
        // }
    }

    public void terminate() {
        // TODO: unload mod/resource here
    }

}

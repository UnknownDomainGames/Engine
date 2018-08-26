package unknowndomain.engine.game;

import com.google.common.collect.ImmutableMap;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.EntityType;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.registry.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;

public abstract class GameBase implements Game {
    protected final Option option;
    protected final EventBus bus;
    protected final ModRepository modRepository;
    protected final ModStore modStore;

    protected GameContext context;
    protected ModManager modManager;

    /**
     * self metadata
     */
    private ModMetadata meta = ModMetadata.Builder.create().setId("unknowndomain")
            .setVersion("0.0.1")
            .setGroup("none").build();

    public GameBase(Option option, ModRepository repository,
                    ModStore store, EventBus bus) {
        this.option = option;
        this.modStore = new ModStore() {
            @Override
            public boolean exists(@Nonnull ModIdentifier identifier) {
                if (meta.equals(identifier)) return true;
                return store.exists(identifier);
            }

            @Nullable
            @Override
            public ModContainer load(@Nonnull ModIdentifier identifier) {
                if (meta.equals(identifier)) return createGameMod();
                return store.load(identifier);
            }

            @Override
            public void store(@Nonnull ModIdentifier identifier, InputStream stream) {
                store.store(identifier, stream);
            }
        };
        this.modRepository = repository;
        this.bus = bus;
        this.option.getMods().add(this.meta);
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        modManager = SimpleModManager.load(
                modStore,
                modRepository,
                option.getMods());
        for (ModContainer mod : modManager.getLoadedMods())
            this.bus.register(mod.getInstance());
    }


    protected ModContainer createGameMod() {
        return new InnerModContainer(meta,
                null, this);
    }


    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        RegistryManager all = SimpleModManager.register(modManager.getLoadedMods(), Registry.Type.of("action", Action.class),
                Registry.Type.of("block", Block.class), Registry.Type.of("item", Item.class),
                Registry.Type.of("entity", EntityType.class));
        this.context = new GameContext(all, bus);
    }

    protected RegistryManager freeze(MutableRegistryManager manager) {
        ImmutableMap.Builder<Class<?>, ImmutableRegistry<?>> builder = ImmutableMap.builder();
        for (Map.Entry<Class<?>, Registry<?>> entry : manager.getEntries())
            builder.put(entry.getKey(), ImmutableRegistry.freeze(entry.getValue()));
        return new FrozenRegistryManager(builder.build());
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

        bus.post(new GameReadyEvent(context));
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

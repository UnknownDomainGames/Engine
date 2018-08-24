package unknowndomain.engine.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.EntityType;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.SimpleModManager;
import unknowndomain.engine.registry.FrozenRegistryManager;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.world.ChunkStore;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GameCommon implements Game {
    protected final Config config;
    protected final EventBus bus;
    protected GameContext context;
    protected Map<String, World> worlds;
    protected List<WorldCommon> internalWorlds;
    protected List<Thread> worldThreads;
    protected ModManager modManager;

    public GameCommon(Config config, EventBus bus) {
        this.config = config;
        this.bus = bus;
    }

    @Override
    public World spawnWorld(World.Config config) {
        if (config == null) {
            WorldCommon w = new WorldCommon(context, new ChunkStore(context));
            this.worlds.put("default", w);
            this.internalWorlds.add(w);
            this.worldThreads.add(new Thread(w));
            return w;
        }
        return null;
    }

    /**
     * Construct stage, collect mod and resource according to it config
     */
    protected void constructStage() {
        this.worlds = Maps.newTreeMap();
        this.internalWorlds = Lists.newArrayList();
        this.worldThreads = Lists.newArrayList();

        modManager = new SimpleModManager(this.bus);

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
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    @Nullable
    @Override
    public World getWorld(String name) {
        return worlds.get(name);
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

        for (Thread thread : this.worldThreads) {
            thread.start();
        }
    }

    public void terminate() {
        for (WorldCommon worldCommon : internalWorlds) {
            worldCommon.stop();
        }

        // TODO: unload mod/resource here
    }

}

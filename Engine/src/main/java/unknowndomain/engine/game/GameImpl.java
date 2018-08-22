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
import unknowndomain.engine.registry.MutableRegistryManager;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.world.ChunkStore;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.World0;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GameImpl implements Game {
    private GameContext context;
    private Map<String, World> worlds;
    private List<World0> internalWorlds;

    // private CyclicBarrier barrier = new CyclicBarrier(parties);

    public void preInit(EventBus bus) {
        RegistryManager all = MutableRegistryManager.collectAll(bus, Registry.Type.of("action", Action.class),
                Registry.Type.of("block", Block.class), Registry.Type.of("item", Item.class),
                Registry.Type.of("entity", EntityType.class));
        this.context = new GameContext(all, bus);
        this.worlds = Maps.newTreeMap();
        this.internalWorlds = Lists.newArrayList();

        World0 w = new World0(context, new ChunkStore(context));
        this.worlds.put("default", w);
        this.internalWorlds.add(w);
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
    public void tick() {
        for (World0 world : internalWorlds) {
            world.tick();
        }
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
}

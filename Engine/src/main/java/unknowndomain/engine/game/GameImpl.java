package unknowndomain.engine.game;

import com.google.common.collect.Lists;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.SimpleRegistryManager;
import unknowndomain.engine.unclassified.EntityType;
import unknowndomain.engine.world.ChunkStore;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.World0;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class GameImpl implements Game {
    private GameContext context;
    private List<Runnable> nextTicks = Lists.newArrayList();
    private World world;

    public void preInit(EventBus bus) {
        RegistryManager all = SimpleRegistryManager.collectAll(
                bus,
                Registry.Type.of("action", Action.class),

                Registry.Type.of("block", Block.class),
                Registry.Type.of("item", Item.class),
                Registry.Type.of("entity", EntityType.class)
        );
        this.context = new GameContext(all, bus, this.nextTicks);
        this.world = new World0(context, new ChunkStore(context));
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public Collection<World> getWorlds() {
        return null;
    }

    @Nullable
    @Override
    public World getWorld(String name) {
        return null;
    }

    @Override
    public void tick() {
        if (nextTicks.size() != 0) {
            for (Runnable tick : nextTicks) { // TODO: limit time
                tick.run();
            }
        }
    }

    @Override
    public void addWorld(World world) {

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

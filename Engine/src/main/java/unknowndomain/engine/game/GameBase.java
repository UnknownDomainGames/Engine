package unknowndomain.engine.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Engine;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.EntityType;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.GamePreInitializationEvent;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.impl.SimpleRegistry;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GameBase implements Game {
    protected final Engine engine;
    protected final Option option;

    protected GameContext context;

    public GameBase(Engine engine, Option option) {
        this.option = option;
        this.engine = engine;
        // this.option.getMods().add(this.meta);
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        // Mod construction moved to Engine
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        // Registry Moved to Engine
//        Map<Class<?>, Registry<?>> maps = Maps.newHashMap();
//        List<SimpleRegistry<?>> registries = Lists.newArrayList();
//        for (Registry.Type<?> tp : Arrays.asList(Registry.Type.of("block", Block.class), Registry.Type.of("item", Item.class), Registry.Type.of("entity", EntityType.class))) {
//            SimpleRegistry<?> registry = new SimpleRegistry<>(tp.type, tp.name);
//            maps.put(tp.type, registry);
//            registries.add(registry);
//        }
//        SimpleRegistryManager manager = new SimpleRegistryManager(maps);
//        eventBus.post(new RegisterEvent(manager));

        GamePreInitializationEvent event = new GamePreInitializationEvent();
        engine.getEventBus().post(event);
        this.context = new GameContext(engine.getRegistryManager(), engine.getEventBus(), event.getBlockAir());
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
        engine.getEventBus().post(new GameReadyEvent(context));
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

    @Nullable
    @Override
    public <T extends Component> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }
}

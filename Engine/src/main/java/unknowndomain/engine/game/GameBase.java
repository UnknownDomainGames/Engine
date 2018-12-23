package unknowndomain.engine.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Engine;
import unknowndomain.engine.action.Action;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.EntityType;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.GamePreInitializationEvent;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.registry.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GameBase implements Game {
    protected final Option option;
    protected final EventBus eventBus;
    protected final ModRepository modRepository;
    protected final ModStore modStore;

    protected GameContext context;
    protected ModManager modManager;

    /**
     * self metadata
     */
    private ModMetadata meta = ModMetadata.Builder.create().setId("unknowndomain").setVersion("0.0.1").setGroup("none")
            .build();

    public GameBase(Option option, ModRepository repository, ModStore store, EventBus eventBus) {
        this.option = option;
        this.modStore = store;
        this.modRepository = repository;
        this.eventBus = eventBus;
        this.option.getMods().add(this.meta);
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        ImmutableMap.Builder<String, ModContainer> idToMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Class, ModContainer> typeToMapBuilder = ImmutableMap.builder();
        ModLoaderWrapper loader = new ModLoaderWrapper().add(new JavaModLoader(modStore));
        decorateLoader(loader);

        List<ModMetadata> mods = option.getMods();
        Map<ModMetadata, ModDependencyEntry[]> map = mods.stream()
                .map(m -> Pair.of(m,
                        m.getDependencies().stream().toArray(ModDependencyEntry[]::new)))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        mods.sort((a, b) -> {
            ModDependencyEntry[] entriesA = map.get(a);
            ModDependencyEntry[] entriesB = map.get(b);
            return 0;
        });
        // TODO: sort here

        for (ModMetadata mod : mods) {
            try {
                if (!modStore.exists(mod)) {
                    if (!modRepository.contains(mod)) {
                        Engine.getLogger()
                                .warn("Cannot find mod " + mod + " from local or other sources! Skip to load!");
                        continue;
                    }
                    modStore.store(mod, modRepository.open(mod));
                }
                ModContainer load = loader.load(mod);
                if (load == null) {
                    Engine.getLogger().warn("Some exceptions happened during loading mod {0} from local! Skip to load!",
                            mod);
                    continue;
                }
                idToMapBuilder.put(mod.getId(), load);
                typeToMapBuilder.put(load.getInstance().getClass(), load);
            } catch (Exception e) {
                Engine.getLogger().warn("Fain to load mod " + mod.getId());
                e.printStackTrace();
            }
        }
        modManager = new SimpleModManager(idToMapBuilder.build(), typeToMapBuilder.build());
        for (ModContainer mod : modManager.getLoadedMods())
            this.eventBus.register(mod.getInstance());
    }

    protected void decorateLoader(ModLoaderWrapper wrapper) {
        wrapper.add(new ModLoader() {
            @Override
            public ModContainer load(ModIdentifier identifier) {
                if (meta.equals(identifier))
                    return new InnerModContainer(meta, null, GameBase.this);
                return null;
            }
        });
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        Map<Class<?>, Registry<?>> maps = Maps.newHashMap();
        List<MutableRegistry<?>> registries = Lists.newArrayList();
        for (Registry.Type<?> tp : Arrays.asList(Registry.Type.of("action", Action.class), Registry.Type.of("block", Block.class),
                Registry.Type.of("item", Item.class), Registry.Type.of("entity", EntityType.class))) {
            MutableRegistry<?> registry = new MutableRegistry<>(tp.type, tp.name);
            maps.put(tp.type, registry);
            registries.add(registry);
        }
        MutableRegistryManager manager = new MutableRegistryManager(maps);
        eventBus.post(new RegisterEvent(manager));
        ImmutableMap.Builder<Class<?>, Registry<?>> builder = ImmutableMap.builder();
        for (Map.Entry<Class<?>, Registry<?>> entry : manager.getEntries())
            builder.put(entry.getKey(), ImmutableRegistry.freeze(entry.getValue()));

        GamePreInitializationEvent event = new GamePreInitializationEvent();
        eventBus.post(event);
        this.context = new GameContext(manager, eventBus, event.getBlockAir());
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

        eventBus.post(new GameReadyEvent(context));
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public boolean isTerminated() {
        return false;
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

package unknowndomain.engine.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.game.GameClientStandalone;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.input.keybinding.KeyBindingManager;
import unknowndomain.engine.client.rendering.Renderer;
import unknowndomain.engine.client.rendering.display.GLFWGameWindow;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureManagerImpl;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EngineEvent;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.*;
import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.mod.misc.EngineDummyContainer;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;
import unknowndomain.engine.util.Side;
import unknowndomain.game.DefaultGameMode;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private GLFWGameWindow window;

    private EventBus eventBus;
    private ModStore modStore;
    private ModRepositoryCollection modRepository;

    private ModManager modManager;
    private RegistryManager registryManager;
    private ResourceManager resourceManager;
    private TextureManager textureManager;
    private KeyBindingManager keybindingManager;
    private List<Renderer.Factory> rendererFactories;

    private Profile playerProfile;
    private GameClientStandalone game;

    EngineClient(int width, int height) {
        window = new GLFWGameWindow(width, height, UnknownDomain.getName());
    }

    @Override
    public void initEngine() {
        Logger log = Engine.getLogger();
        log.info("Initializing Window!");
        window.init();

        eventBus = new AsmEventBus();
        // TODO: Move it
        eventBus.register(new DefaultGameMode());
        modStore = new ModStoreLocal(Paths.get("mods"));
        modRepository = new ModRepositoryCollection();
        playerProfile = new Profile(UUID.randomUUID(), 12);

        // Construction Stage
        log.info("Constructing Mods!");
        eventBus.post(new EngineEvent.ModConstructionStart(this));
        constructMods();
        log.info("Initializing Mods!");
        eventBus.post(new EngineEvent.ModInitializationEvent(this));
        log.info("Finishing Construction!");
        eventBus.post(new EngineEvent.ModConstructionFinish(this));

        // Registration Stage
        log.info("Creating Registry Manager!");
        Map<Class<?>, Registry<?>> registries = Maps.newHashMap();
        eventBus.post(new EngineEvent.RegistryConstructionEvent(this, registries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries));
        log.info("Registering!");
        eventBus.post(new EngineEvent.RegistrationStart(this, registryManager));
        log.info("Finishing Registration!");
        eventBus.post(new EngineEvent.RegistrationFinish(this, registryManager));

        // Resource Stage
        // Later when separating common and client, common will not have this part
        log.info("Loading Client-only stuff");
        keybindingManager = new KeyBindingManager(registryManager.getRegistry(KeyBinding.class));
        keybindingManager.reload();
        window.addKeyCallback(keybindingManager::handleKey);
        window.addMouseCallback(keybindingManager::handleMouse);

        resourceManager = new ResourceManagerImpl();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());
        textureManager = new TextureManagerImpl();
        rendererFactories = Lists.newArrayList();

        eventBus.post(new EngineEvent.ResourceConstructionStart(this, resourceManager, textureManager, rendererFactories));
        eventBus.post(new EngineEvent.ResourceConstructionFinish(this, resourceManager, textureManager, rendererFactories));

        // Finish Stage
        log.info("Finishing Initialization!");
        eventBus.post(new EngineEvent.InitializationComplete(this));
    }

    private void constructMods() {
        ImmutableMap.Builder<String, ModContainer> idToMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Class, ModContainer> typeToMapBuilder = ImmutableMap.builder();

        // Add engine container
        EngineDummyContainer engine = new EngineDummyContainer();
        idToMapBuilder.put(engine.getModId(), engine);
        typeToMapBuilder.put(engine.getInstance().getClass(), engine);

        ModLoaderWrapper loader = new ModLoaderWrapper().add(new JavaModLoader(modStore));

        List<ModMetadata> mods = Collections.emptyList();// TODO: Scan mods
        Map<ModMetadata, ModDependencyEntry[]> map = mods.stream().map(m -> Pair.of(m, m.getDependencies().stream().toArray(ModDependencyEntry[]::new)))
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        // TODO: Topological Sort
        mods.sort((a, b) -> {
            ModDependencyEntry[] entriesA = map.get(a);
            ModDependencyEntry[] entriesB = map.get(b);
            return 0;
        });

        for (ModMetadata mod : mods) {
            try {
                if (!modStore.exists(mod)) {
                    if (!modRepository.contains(mod)) {
                        Engine.getLogger().warn("Cannot find mod " + mod + " from local or other sources! Skip to load!");
                        continue;
                    }
                    modStore.store(mod, modRepository.open(mod));
                }
                ModContainer load = loader.load(mod);
                if (load == null) {
                    Engine.getLogger().warn("Some exceptions happened during loading mod {0} from local! Skip to load!", mod);
                    continue;
                }
                idToMapBuilder.put(mod.getId(), load);
                typeToMapBuilder.put(load.getInstance().getClass(), load);
            } catch (Exception e) {
                Engine.getLogger().warn("Fain to load mod " + mod.getId());
                e.printStackTrace();
            }
        }
        // ImmutableMap<String, ModContainer> loadedMods = ;
        modManager = new SimpleModManager(idToMapBuilder.build(), typeToMapBuilder.build());
        Collection<ModContainer> loadedMods = modManager.getLoadedMods();
        Engine.LOGGER.info("Engine has successfully loaded " + loadedMods.size() + " mods:");
        for (ModContainer mod : modManager.getLoadedMods()) {
            this.eventBus.register(mod.getInstance());
            Engine.LOGGER.debug("  Loaded: " + mod.getModId());
        }

    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public Game startGame(Game.Option option) {
        // prepare
        game = new GameClientStandalone(this, new Game.Option(Lists.newArrayList(), Lists.newArrayList()));
        game.run();

        // stop last game
        return game;
    }

    @Override
    public GameClientStandalone getCurrentGame() {
        return game;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    public GLFWGameWindow getWindow() {
        return window;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public List<Renderer.Factory> getRendererFactories() {
        return rendererFactories;
    }

    @Override
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keybindingManager;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }
}

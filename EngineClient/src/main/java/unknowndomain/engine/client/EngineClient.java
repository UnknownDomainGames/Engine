package unknowndomain.engine.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import unknowndomain.engine.Engine;
import unknowndomain.engine.Platform;
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
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.impl.DefaultModManager;
import unknowndomain.engine.mod.util.ModCollector;
import unknowndomain.engine.player.Profile;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;
import unknowndomain.engine.util.Side;
import unknowndomain.game.DefaultGameMode;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.SystemUtils.*;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private GLFWGameWindow window;

    private EventBus eventBus;
//    private ModStore modStore;
//    private ModRepositoryCollection modRepository;

    private ModManager modManager;
    private RegistryManager registryManager;
    @Deprecated
    private ResourceManager resourceManager;
    private TextureManager textureManager;
    private KeyBindingManager keybindingManager;
    private List<Renderer.Factory> rendererFactories;

    private Profile playerProfile;
    private GameClientStandalone game;

    EngineClient(int width, int height) { // TODO: Remove it
        window = new GLFWGameWindow(width, height, UnknownDomain.getName());
    }

    @Override
    public void initEngine() {
        paintSystemInfo();

        Logger log = Engine.getLogger();
        log.info("Initializing Window!");
        window.init();

        eventBus = new AsmEventBus();
        // TODO: Move it
        eventBus.register(new DefaultGameMode());

        playerProfile = new Profile(UUID.randomUUID(), 12);

        // Load mod
//        modStore = new ModStoreLocal(Paths.get("mods"));
//        modRepository = new ModRepositoryCollection();


        // Construction Stage
//        log.info("Constructing Mods!");
//        eventBus.post(new EngineEvent.ModConstructionStart(this));
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
        modManager = new DefaultModManager();

        Path modFolder = Paths.get("mods");
        if(!Files.exists(modFolder)) {
            try {
                Files.createDirectory(modFolder);
            } catch (IOException e) {
                Platform.getLogger().warn(e.getMessage(), e);
            }
        }

        try {
            Collection<ModContainer> modContainers = modManager.loadMod(ModCollector.createFolderModCollector(modFolder));
            modContainers.forEach(modContainer -> getEventBus().register(modContainer.getInstance()));
            modContainers.forEach(modContainer -> Platform.getLogger().info("Loaded mod: {}", modContainer.getModId()));
        } catch (IOException e) {
            Platform.getLogger().warn(e.getMessage(), e);
        }
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void startGame() {
        // prepare
        game = new GameClientStandalone(this);
        game.run();
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

    private void paintSystemInfo() {
        Logger logger = Platform.getLogger();
        logger.info("----- System Information -----");
        logger.info("\tOperating system: " + OS_NAME + " (" + OS_ARCH + ") version " + OS_VERSION);
        logger.info("\tJava version: " + JAVA_VERSION + ", " + JAVA_VENDOR);
        logger.info("\tJVM info: " + JAVA_VM_NAME + " (" + JAVA_VM_INFO + "), " + JAVA_VM_VENDOR);
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        logger.info("\tMax memory: " + maxMemory + " bytes (" + maxMemory / 1024L / 1024L + " MB)");
        logger.info("\tTotal memory: " + totalMemory + " bytes (" + totalMemory / 1024L / 1024L + " MB)");
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmFlags = runtimeMXBean.getInputArguments();
        StringBuilder formattedFlags = new StringBuilder();
        for (String flag : jvmFlags) {
            if (formattedFlags.length() > 0) {
                formattedFlags.append(' ');
            }
            formattedFlags.append(flag);
        }
        logger.info("\tJVM flags (" + jvmFlags.size() + " totals): " + formattedFlags.toString());
        logger.info("------------------------------");
    }
}

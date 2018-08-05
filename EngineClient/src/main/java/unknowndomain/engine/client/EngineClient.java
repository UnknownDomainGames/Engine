package unknowndomain.engine.client;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.Engine;
import unknowndomain.engine.client.block.Player;
import unknowndomain.engine.client.display.Camera;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.client.world.EasyWorld;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.Timer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.rendering.shader.CreateShaderNode;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.rendering.RenderDebug;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.model.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.model.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.model.pipeline.ResolveTextureUVNode;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.unclassified.BlockObjectBuilder;

import java.util.List;
import java.util.Map;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private DefaultGameWindow window;
    private RendererGlobal renderer;

    /*
     * Managers section
     */

    private ResourceManagerImpl resourceManager;
    private KeyBindingManager keyBindingManager;

    private GameClientImpl game;
    BlockObject testObj;
    private EasyWorld world;

    private Timer timer;

    public EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());

        init();
        gameLoop();
    }

    private Player player;

    public void init() {
        window.init();


        keyBindingManager = new KeyBindingManager(resourceManager);
        resourceManager = new ResourceManagerImpl();
        renderer = new RendererGlobal();
        player = new Player(renderer.getCamera());

        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        resourceManager.add("BlockModels",
                new ResolveModelsNode(),
                new ResolveTextureUVNode(),
                new ModelToMeshNode(),
                new MeshToGLNode())
                .subscribe("BlockModels", resourceManager);
        resourceManager.subscribe("TextureMap", resourceManager);
        resourceManager.add("Shader", new CreateShaderNode());
//                .subscribe("Shader", renderer);
        keyBindingManager.update();
        test();

        timer = new Timer();
        timer.init();
    }

    private void test() {
        try {
            Shader v = new Shader(0, ShaderType.VERTEX_SHADER);
            v.loadShader("assets/unknowndomain/shader/common.vert");
            Shader f = new Shader(0, ShaderType.FRAGMENT_SHADER);
            f.loadShader("assets/unknowndomain/shader/common.frag");


            Map<BlockObject, GLMesh> map = Maps.newHashMap();
            world = new EasyWorld();
            RenderDebug easy = new RenderDebug(v, f, world, map);
            resourceManager.subscribe("TextureMap", easy);

            BlockObject stone = BlockObjectBuilder.create().build().get(0);
            List<GLMesh> meshList = resourceManager.push("BlockModels", Lists.newArrayList(
                    new ResourcePath("", "/minecraft/models/block/stone.json")
//                    new ResourcePath("", "/minecraft/models/block/sand.json"),
//                    new ResourcePath("", "/minecraft/models/block/brick.json"),
//                    new ResourcePath("", "/minecraft/models/block/clay.json"),
//                    new ResourcePath("", "/minecraft/models/block/furnace.json")
//                    new ResourcePath("", "/minecraft/models/block/birch_stairs.json"),
//                    new ResourcePath("", "/minecraft/models/block/lever.json"),
            ));
            testObj = stone;
            map.put(stone, meshList.get(0));

            world.setBlock(new BlockPos(0, 0, 0), stone);
            world.setBlock(new BlockPos(1, 0, 0), stone);
            world.setBlock(new BlockPos(2, 0, 0), stone);
            world.setBlock(new BlockPos(3, 0, 0), stone);

            renderer.add(easy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {

    }

    public void terminate() {

    }

    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / 30.0f;
        while (!window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                // update(interval); //TODO: game logic
                // System.out.println("tick");
                // game.tick();
                accumulator -= interval;
            }

            window.update();
            player.update();

            sync(); // TODO: check if use v-sync first
        }
    }

    private void sync() {
        float loopSlot = 1f / 60.0f;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    boolean paused = false;

    public void handleCursorMove(double x, double y) {
        if (!paused)
            renderer.getCamera().rotate((float) x, (float) y);
    }

    public void handleKeyPress(int key, int scancode, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                getKeyBindingManager().handlePress(key, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                getKeyBindingManager().handleRelease(key, modifiers);
                break;
            default:
                break;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            if (paused) {
                window.hideCursor();
                paused = false;
            } else {
                window.showCursor();
                paused = true;
            }
        }
    }

    public void handleTextInput(int codepoint, int modifiers) {
    }

    public void handleMousePress(int button, int action, int modifiers) {
        switch (action) {
            case GLFW.GLFW_PRESS:
                getKeyBindingManager().handlePress(button + 400, modifiers);
                break;
            case GLFW.GLFW_RELEASE:
                getKeyBindingManager().handleRelease(button + 400, modifiers);
                break;
            default:
                break;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            BlockPos pick = world.pickBeside(renderer.getCamera().getPosition(), renderer.getCamera().getFrontVector(), 10);
            if (pick != null)
                world.setBlock(pick, testObj);
        }
    }

    public void handleScroll(double xoffset, double yoffset) {
        // renderer.getCamera().zoom((-yoffset / window.getHeight() * 2 + 1)*1.5);
    }

    public RendererGlobal getRenderer() {
        return renderer;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    @Override
    public ModManager getModManager() {
        return null; // TODO Inject Mod Manager
    }

    @Override
    public ResourceManager getResourcePackManager() {
        return null;
    }

    @Override
    public Game getGame() {
        return null;
    }
}

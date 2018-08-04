package unknowndomain.engine.client;

import com.google.common.collect.Lists;

import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.api.Engine;
import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderType;
import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.math.Timer;
import unknowndomain.engine.api.mod.ModManager;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.client.rendering.shader.CreateShaderNode;
import unknowndomain.engine.client.resource.ResourceManagerImpl;
import unknowndomain.engine.api.resource.ResourcePath;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.rendering.RenderDebug;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.resource.pipeline.ModelToMeshNode;
import unknowndomain.engine.client.resource.pipeline.ResolveModelsNode;
import unknowndomain.engine.client.resource.pipeline.ResolveTextureUVNode;

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

    private Timer timer;

    public EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());

        init();
        gameLoop();
    }

    public void init() {
        window.init();

        keyBindingManager = new KeyBindingManager(resourceManager);
        resourceManager = new ResourceManagerImpl();
        renderer = new RendererGlobal();

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
//            EnumMap<ShaderType, Shader> push = resourceManager.push("Shader", "common");
            Shader v = new Shader(0, ShaderType.VERTEX_SHADER);
            v.loadShader("assets/unknowndomain/shader/common.vert");
            Shader f = new Shader(0, ShaderType.FRAGMENT_SHADER);
            f.loadShader("assets/unknowndomain/shader/common.frag");
//            RendererShaderProgramCommon common = new RendererShaderProgramCommon(v, f);
//            renderer.add(common);
            RenderDebug block = new RenderDebug(v, f);
            renderer.add(block);
            resourceManager.subscribe("BlockModels", block);
            resourceManager.subscribe("TextureMap", block);
            resourceManager.push("BlockModels", Lists.newArrayList(
//                    new ResourcePath("", "/minecraft/models/block/stone.json"),
//                    new ResourcePath("", "/minecraft/models/block/sand.json"),
//                    new ResourcePath("", "/minecraft/models/block/brick.json"),
//                    new ResourcePath("", "/minecraft/models/block/clay.json"),
                    new ResourcePath("", "/minecraft/models/block/furnace.json")
//                    new ResourcePath("", "/minecraft/models/block/birch_stairs.json"),
//                    new ResourcePath("", "/minecraft/models/block/lever.json"),
            ));
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

    public void handleCursorMove(double x, double y) {
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
            GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
        }
        Camera camera = renderer.getCamera();
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
            switch (key) {
                case GLFW.GLFW_KEY_W:
                    camera.forward();
                    break;
                case GLFW.GLFW_KEY_S:
                    camera.backward();
                    break;
                case GLFW.GLFW_KEY_A:
                    camera.left();
                    break;
                case GLFW.GLFW_KEY_D:
                    camera.right();
                    break;
                case GLFW.GLFW_KEY_SPACE:
                    camera.move(0, 1, 0);
                    break;
                case GLFW.GLFW_KEY_LEFT_SHIFT:
                case GLFW.GLFW_KEY_RIGHT_SHIFT:
                    camera.move(0, -0.1f, 0);
                    break;
                case GLFW.GLFW_KEY_Q:
                    break;
                case GLFW.GLFW_KEY_E:
                    break;
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

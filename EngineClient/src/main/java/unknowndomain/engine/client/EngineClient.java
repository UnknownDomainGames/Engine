package unknowndomain.engine.client;

import java.util.List;

import com.google.common.collect.Lists;

import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.api.Engine;
import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.math.Timer;
import unknowndomain.engine.api.mod.ModManager;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.display.DefaultGameWindow;
import unknowndomain.engine.client.keybinding.KeyBindingManager;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.rendering.RenderBlock;
import unknowndomain.engine.client.rendering.RendererGlobal;
import unknowndomain.engine.client.resource.ResourceSourceBuiltin;
import unknowndomain.engine.client.resource.pipeline.MinecraftPipeline;
import unknowndomain.engine.client.resource.pipeline.ResourcePipeline;
import unknowndomain.engine.client.resource.pipeline.ResourcePipeline.Node;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.model.GLMesh;;

//import unknowndomain.engine.api.resource.ResourcePackManager;

public class EngineClient implements Engine {

    /*
     * Rendering section
     */

    private DefaultGameWindow window;
    private RendererGlobal renderer;

    /*
     * Managers section
     */

    private ResourceManager resourceManager;
    private KeyBindingManager keyBindingManager;

    private GameClientImpl game;

    private Timer timer;

    public EngineClient(int width, int height) {
        window = new DefaultGameWindow(this, width, height, UnknownDomain.getName());

        resourceManager = new ResourceManager();
        resourceManager.addResourceSource(new ResourceSourceBuiltin());

        // modelManager = new ModelManager(resourceManager);
        // textureManager = new TextureManager(resourceManager);
        keyBindingManager = new KeyBindingManager(resourceManager);

        init();
        gameLoop();
    }

    public void init() {
        window.init();

        keyBindingManager.update();
        renderer = new RendererGlobal();
        test();

        timer = new Timer();
        timer.init();
    }

    private void test() {
        RenderBlock b = new RenderBlock(renderer.getCamera(), null, null);
        ResourcePipeline pipeline = MinecraftPipeline.create(resourceManager);
        pipeline.add("BakeModels", new MeshToGLNode()).add("BakeModels", (context, in) -> {
            List<GLMesh> meshes = (List<GLMesh>) in;
            b.setMesh(meshes.get(0));
            return null;
        });
        pipeline.add("TextureMap", (context, in) -> {
            GLTexture text = (GLTexture) in;
            b.setTexture(text);
            return null;
        });
        try {
            pipeline.push("BakeModels", Lists.newArrayList(new DomainedPath("", "/minecraft/models/block/stone.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderer.add(b);
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

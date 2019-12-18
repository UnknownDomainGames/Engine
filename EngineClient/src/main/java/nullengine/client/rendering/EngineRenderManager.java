package nullengine.client.rendering;

import nullengine.Platform;
import nullengine.client.EngineClient;
import nullengine.client.asset.AssetType;
import nullengine.client.event.rendering.CameraChangeEvent;
import nullengine.client.gui.EngineGuiManager;
import nullengine.client.gui.GuiManager;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.camera.FixedCamera;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.gui.GuiRenderer;
import nullengine.client.rendering.texture.EngineTextureManager;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.client.rendering.util.GPUInfo;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class EngineRenderManager implements RenderManager {

    private final EngineClient engine;

    private Thread renderThread;
    private Window window;
    private Matrix4f projection = new Matrix4f();
    private EngineTextureManager textureManager;
    private EngineGuiManager guiManager;
    private GuiRenderer guiRenderer;

    private Camera camera;
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    public EngineRenderManager(EngineClient engine) {
        this.engine = engine;
    }

    @Override
    public EngineClient getEngine() {
        return engine;
    }

    @Override
    public Thread getRenderThread() {
        return renderThread;
    }

    @Override
    public boolean isRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public Matrix4fc getProjectionMatrix() {
        return projection;
    }

    @Override
    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Override
    public GuiManager getGuiManager() {
        return guiManager;
    }

    @Override
    public GPUInfo getGPUInfo() {
        return RenderEngine.getManager().getGPUInfo();
    }

    private long lastUpdateFps = System.currentTimeMillis();
    private int frameCount = 0;
    private int fps = 0;

    @Override
    public int getFPS() {
        return fps;
    }

    private void updateFPS() {
        long time = System.currentTimeMillis();
        if (time - lastUpdateFps > 1000) {
            fps = frameCount;
            frameCount = 0; // reset the FPS counter
            lastUpdateFps += 1000; // add one second
        }
        frameCount++;
    }

    @Override
    @Nonnull
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(@Nonnull Camera camera) {
        if (this.camera == camera) {
            return;
        }

        this.camera = notNull(camera);
        getEngine().getEventBus().post(new CameraChangeEvent(camera));
    }

    @Override
    public FrustumIntersection getFrustumIntersection() {
        return frustumIntersection;
    }

    public void render(float partial) {
        guiManager.doTick();

        if (window.isResized()) {
            projection.identity().perspective((float) Math.toRadians(60),
                    (float) window.getWidth() / window.getHeight(), 0.01f, 1000f);
        }
        camera.update(partial);
        frustumIntersection.set(projection.mul(camera.getViewMatrix(), new Matrix4f()));

        glClear(GL_COLOR_BUFFER_BIT);
        RenderEngine.doRender(partial);
        glfwPollEvents();
        updateFPS();
    }

    public void init(Thread renderThread) {
        this.renderThread = renderThread;

        RenderEngine.start(new RenderEngine.Settings()
                .swapBuffersListener((manager1, partial) -> guiRenderer.render(partial)));

        nullengine.client.rendering.management.RenderManager manager = RenderEngine.getManager();
        window = manager.getPrimaryWindow();
        window.setDisplayMode(Platform.getEngineClient().getSettings().getDisplaySettings().getDisplayMode(), Platform.getEngineClient().getSettings().getDisplaySettings().getResolutionWidth(), Platform.getEngineClient().getSettings().getDisplaySettings().getResolutionHeight(), Platform.getEngineClient().getSettings().getDisplaySettings().getFrameRate());

        initTexture();
        guiManager = new EngineGuiManager(window);
        guiRenderer = new GuiRenderer(window, guiManager);

        camera = new FixedCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));

        window.show();
    }

    private void initTexture() {
        textureManager = new EngineTextureManager();
        TextureManager.Internal.setInstance(textureManager);
        getEngine().getAssetManager().register(AssetType.builder(GLTexture2D.class).name("Texture").provider(textureManager).parentLocation("texture").extensionName(".png").build());
    }

    public void dispose() {
        guiRenderer.dispose();
        RenderEngine.stop();
    }
}

package engine.graphics.glfw;

import engine.graphics.display.Screen;
import engine.graphics.display.VideoMode;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public final class GLFWScreen implements Screen {
    private final long handle;
    private final String name;
    private final int workareaX;
    private final int workareaY;
    private final int workareaWidth;
    private final int workareaHeight;
    private final int physicsWidth;
    private final int physicsHeight;
    private float scaleX;
    private float scaleY;
    private final VideoMode videoMode;
    private final List<VideoMode> videoModes;

    public GLFWScreen(long handle) {
        this.handle = handle;
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer workareaX = memoryStack.mallocInt(1);
            IntBuffer workareaY = memoryStack.mallocInt(1);
            IntBuffer workareaWidth = memoryStack.mallocInt(1);
            IntBuffer workareaHeight = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorWorkarea(handle, workareaX, workareaY, workareaWidth, workareaHeight);
            IntBuffer physicsWidth = memoryStack.mallocInt(1);
            IntBuffer physicsHeight = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorPhysicalSize(handle, physicsWidth, physicsHeight);
            FloatBuffer xScale = memoryStack.mallocFloat(1);
            FloatBuffer yScale = memoryStack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(handle, xScale, yScale);

            this.name = GLFW.glfwGetMonitorName(handle);
            this.workareaX = workareaX.get();
            this.workareaY = workareaY.get();
            this.workareaWidth = workareaWidth.get();
            this.workareaHeight = workareaHeight.get();
            this.physicsWidth = physicsWidth.get();
            this.physicsHeight = physicsHeight.get();
            this.scaleX = xScale.get();
            this.scaleY = yScale.get();
            this.videoMode = createVideoMode(GLFW.glfwGetVideoMode(handle));
            this.videoModes = createVideoModes(GLFW.glfwGetVideoModes(handle));
        }
    }

    private static List<VideoMode> createVideoModes(GLFWVidMode.Buffer vidModes) {
        final int length = vidModes.limit();
        final VideoMode[] array = new VideoMode[length];
        for (int i = 0; i < length; i++) {
            array[i] = createVideoMode(vidModes.get(i));
        }
        return List.of(array);
    }

    private static VideoMode createVideoMode(GLFWVidMode vidMode) {
        return new VideoMode(vidMode.width(), vidMode.height(), vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(), vidMode.refreshRate());
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWorkareaX() {
        return workareaX;
    }

    @Override
    public int getWorkareaY() {
        return workareaY;
    }

    @Override
    public int getWorkareaWidth() {
        return workareaWidth;
    }

    @Override
    public int getWorkareaHeight() {
        return workareaHeight;
    }

    @Override
    public int getPhysicsWidth() {
        return physicsWidth;
    }

    @Override
    public int getPhysicsHeight() {
        return physicsHeight;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public VideoMode getVideoMode() {
        return videoMode;
    }

    @Override
    public List<VideoMode> getVideoModes() {
        return videoModes;
    }

    void refreshScale() {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer xScale = memoryStack.mallocFloat(1);
            FloatBuffer yScale = memoryStack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(handle, xScale, yScale);
            this.scaleX = xScale.get();
            this.scaleY = yScale.get();
        }
    }
}

package engine.graphics.glfw;

import engine.graphics.display.Screen;
import engine.graphics.display.VideoMode;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLFWScreen implements Screen {
    private final long pointer;
    private String name;

    private int workareaX;
    private int workareaY;
    private int workareaWidth;
    private int workareaHeight;

    private int physicsWidth;
    private int physicsHeight;

    private float scaleX;
    private float scaleY;

    private VideoMode videoMode;
    private final List<VideoMode> videoModes;

    public GLFWScreen(long pointer) {
        this.pointer = pointer;
        videoModes = new ArrayList<>();
    }

    @Override
    public long getPointer() {
        return pointer;
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

    public void refresh() {
        name = GLFW.glfwGetMonitorName(pointer);
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer workareaX = memoryStack.mallocInt(1);
            IntBuffer workareaY = memoryStack.mallocInt(1);
            IntBuffer workareaWidth = memoryStack.mallocInt(1);
            IntBuffer workareaHeight = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorWorkarea(pointer, workareaX, workareaY, workareaWidth, workareaHeight);
            IntBuffer physicsWidth = memoryStack.mallocInt(1);
            IntBuffer physicsHeight = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorPhysicalSize(pointer, physicsWidth, physicsHeight);
            FloatBuffer xScale = memoryStack.mallocFloat(1);
            FloatBuffer yScale = memoryStack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(pointer, xScale, yScale);

            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(pointer);
            GLFWVidMode.Buffer vidModes = GLFW.glfwGetVideoModes(pointer);
            this.workareaX = workareaX.get();
            this.workareaY = workareaY.get();
            this.workareaWidth = workareaWidth.get();
            this.workareaHeight = workareaHeight.get();
            this.physicsWidth = physicsWidth.get();
            this.physicsHeight = physicsHeight.get();
            this.scaleX = xScale.get();
            this.scaleY = yScale.get();
            this.videoMode = createVideoMode(vidMode);
            this.videoModes.clear();
            this.videoModes.addAll(createVideoModes(vidModes));
        }
    }

    private static List<VideoMode> createVideoModes(GLFWVidMode.Buffer vidModes) {
        List<VideoMode> videoModes = new ArrayList<>();
        for (int i = 0; i < vidModes.limit(); i++) {
            videoModes.add(createVideoMode(vidModes.get()));
        }
        return List.copyOf(videoModes);
    }

    private static VideoMode createVideoMode(GLFWVidMode vidMode) {
        return new VideoMode(vidMode.width(), vidMode.height(), vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(), vidMode.refreshRate());
    }
}

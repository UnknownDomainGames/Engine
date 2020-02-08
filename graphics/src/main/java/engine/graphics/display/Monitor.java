package engine.graphics.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Monitor {
    private final long pointer;
    private String name;

    private int physicsWidth;
    private int physicsHeight;

    private float scaleX;
    private float scaleY;

    private int posX;
    private int posY;

    private VideoMode videoMode;
    private final List<VideoMode> videoModes;

    public Monitor(long pointer){
        this.pointer = pointer;
        videoModes = new ArrayList<>();
    }

    public Monitor(long pointer, String name, int physicsWidth, int physicsHeight, float scaleX, float scaleY, int posX, int posY, VideoMode videoMode, List<VideoMode> videoModes) {
        this.pointer = pointer;
        this.name = name;
        this.physicsWidth = physicsWidth;
        this.physicsHeight = physicsHeight;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.posX = posX;
        this.posY = posY;
        this.videoMode = videoMode;
        this.videoModes = videoModes;
    }

    public long getPointer() {
        return pointer;
    }

    public String getName() {
        return name;
    }

    public int getPhysicsWidth() {
        return physicsWidth;
    }

    public int getPhysicsHeight() {
        return physicsHeight;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public VideoMode getVideoMode() {
        return videoMode;
    }

    public List<VideoMode> getVideoModes() {
        return videoModes;
    }

    public void refreshMonitor() {
        name = GLFW.glfwGetMonitorName(pointer);
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            IntBuffer width = memoryStack.mallocInt(1);
            IntBuffer height = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorPhysicalSize(pointer, width, height);
            FloatBuffer xScale = memoryStack.mallocFloat(1);
            FloatBuffer yScale = memoryStack.mallocFloat(1);
            GLFW.glfwGetMonitorContentScale(pointer, xScale, yScale);
            IntBuffer xPos = memoryStack.mallocInt(1);
            IntBuffer yPos = memoryStack.mallocInt(1);
            GLFW.glfwGetMonitorPos(pointer, xPos, yPos);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(pointer);
            GLFWVidMode.Buffer vidModes = GLFW.glfwGetVideoModes(pointer);
            this.physicsWidth = width.get();
            this.physicsHeight = height.get();
            this.scaleX = xScale.get();
            this.scaleY = yScale.get();
            this.posX = xPos.get();
            this.posY = yPos.get();
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

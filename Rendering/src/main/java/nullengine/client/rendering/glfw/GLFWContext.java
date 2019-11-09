package nullengine.client.rendering.glfw;

import nullengine.client.rendering.display.Monitor;
import nullengine.client.rendering.display.VideoMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwInit;

public final class GLFWContext {

    public static final Logger LOGGER = LoggerFactory.getLogger("GLFW");

    private static Monitor primaryMonitor;
    private static List<Monitor> monitors;

    public static Monitor getPrimaryMonitor() {
        return primaryMonitor;
    }

    public static List<Monitor> getMonitors() {
        return monitors;
    }

    public static void initialize() {
        LOGGER.info("Initializing GLFW context!");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        initMonitor();
    }

    private static void initMonitor() {
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        List<Monitor> monitors = new ArrayList<>();
        for (int i = 0; i < pointerBuffer.capacity(); i++) {
            monitors.add(createMonitor(pointerBuffer.get()));
        }
        GLFWContext.monitors = List.copyOf(monitors);

        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        GLFWContext.primaryMonitor = monitors.stream().filter(monitor -> monitor.getPointer() == primaryMonitor).findAny().orElseThrow();
    }

    private static Monitor createMonitor(long pointer) {
        String name = GLFW.glfwGetMonitorName(pointer);
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
            return new Monitor(pointer, name, width.get(), height.get(), xScale.get(), yScale.get(), xPos.get(), yPos.get(),
                    createVideoMode(vidMode), createVideoModes(vidModes));
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

    private GLFWContext() {
    }
}

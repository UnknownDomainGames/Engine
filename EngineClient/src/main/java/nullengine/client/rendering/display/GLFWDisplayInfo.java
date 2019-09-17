package nullengine.client.rendering.display;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLFWDisplayInfo implements DisplayInfo {

    private final Monitor primaryMonitor;
    private final List<Monitor> monitors;

    public GLFWDisplayInfo() {
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        List<Monitor> monitors = new ArrayList<>();
        for (int i = 0; i < pointerBuffer.capacity(); i++) {
            monitors.add(createMonitor(pointerBuffer.get()));
        }
        this.monitors = List.copyOf(monitors);

        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        this.primaryMonitor = monitors.stream().filter(monitor -> monitor.getPointer() == primaryMonitor).findAny().orElseThrow();
    }

    private Monitor createMonitor(long pointer) {
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

    private List<VideoMode> createVideoModes(GLFWVidMode.Buffer vidModes) {
        List<VideoMode> videoModes = new ArrayList<>();
        for (int i = 0; i < vidModes.limit(); i++) {
            videoModes.add(createVideoMode(vidModes.get()));
        }
        return List.copyOf(videoModes);
    }

    private VideoMode createVideoMode(GLFWVidMode vidMode) {
        return new VideoMode(vidMode.width(), vidMode.height(), vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(), vidMode.refreshRate());
    }

    @Override
    public Monitor getPrimaryMonitor() {
        return primaryMonitor;
    }

    @Override
    public List<Monitor> getMonitors() {
        return monitors;
    }
}

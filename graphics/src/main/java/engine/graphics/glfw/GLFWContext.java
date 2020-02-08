package engine.graphics.glfw;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import engine.graphics.display.Monitor;
import engine.graphics.display.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public final class GLFWContext {

    public static final Logger LOGGER = LoggerFactory.getLogger("GLFW");

    private static boolean initialized;
    private static boolean terminated;

    private static Monitor primaryMonitor;
    private static Map<Long, Monitor> monitors;

    private static ObservableList<Window> showingWindows = ObservableCollections.observableList(new ArrayList<>());
    private static ObservableList<Window> unmodifiableShowingWindows = ObservableCollections.unmodifiableObservableList(showingWindows);

    public static Monitor getPrimaryMonitor() {
        return primaryMonitor;
    }

    public static Collection<Monitor> getMonitors() {
        return monitors.values();
    }

    public static Monitor getMonitor(long pointer) {
        return monitors.get(pointer);
    }

    public static synchronized void initialize() {
        if (initialized) return;
        initialized = true;
        LOGGER.info("Initializing GLFW context!");
        GLFWErrorCallback.createThrow().set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        initMonitor();
    }

    public static synchronized void terminate() {
        if (terminated) return;
        terminated = true;
        glfwTerminate();
    }

    private static void initMonitor() {
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        Map<Long, Monitor> monitors = new HashMap<>();
        for (int i = 0; i < pointerBuffer.capacity(); i++) {
            long pointer = pointerBuffer.get();
            monitors.put(pointer, createMonitor(pointer));
        }
        GLFWContext.monitors = Map.copyOf(monitors);
        GLFWContext.primaryMonitor = getMonitor(GLFW.glfwGetPrimaryMonitor());
    }

    private static Monitor createMonitor(long pointer) {
//        String name = GLFW.glfwGetMonitorName(pointer);
//        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
//            IntBuffer width = memoryStack.mallocInt(1);
//            IntBuffer height = memoryStack.mallocInt(1);
//            GLFW.glfwGetMonitorPhysicalSize(pointer, width, height);
//            FloatBuffer xScale = memoryStack.mallocFloat(1);
//            FloatBuffer yScale = memoryStack.mallocFloat(1);
//            GLFW.glfwGetMonitorContentScale(pointer, xScale, yScale);
//            IntBuffer xPos = memoryStack.mallocInt(1);
//            IntBuffer yPos = memoryStack.mallocInt(1);
//            GLFW.glfwGetMonitorPos(pointer, xPos, yPos);
//            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(pointer);
//            GLFWVidMode.Buffer vidModes = GLFW.glfwGetVideoModes(pointer);
//            return new Monitor(pointer, name, width.get(), height.get(), xScale.get(), yScale.get(), xPos.get(), yPos.get(),
//                    createVideoMode(vidMode), createVideoModes(vidModes));
//        }
        var monitor = new Monitor(pointer);
        monitor.refreshMonitor();
        return monitor;
    }

    public static ObservableList<Window> getShowingWindows() {
        return unmodifiableShowingWindows;
    }

    static void onShowedWindow(GLFWWindow window) {
        showingWindows.add(window);
    }

    static void onHidedWindow(GLFWWindow window) {
        showingWindows.remove(window);
    }

//    private static List<VideoMode> createVideoModes(GLFWVidMode.Buffer vidModes) {
//        List<VideoMode> videoModes = new ArrayList<>();
//        for (int i = 0; i < vidModes.limit(); i++) {
//            videoModes.add(createVideoMode(vidModes.get()));
//        }
//        return List.copyOf(videoModes);
//    }
//
//    private static VideoMode createVideoMode(GLFWVidMode vidMode) {
//        return new VideoMode(vidMode.width(), vidMode.height(), vidMode.redBits(), vidMode.greenBits(), vidMode.blueBits(), vidMode.refreshRate());
//    }

    private GLFWContext() {
    }
}

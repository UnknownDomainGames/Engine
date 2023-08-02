package engine.graphics.glfw;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import engine.graphics.display.Screen;
import engine.graphics.display.VideoMode;
import engine.graphics.display.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public final class GLFWContext {

    public static final Logger LOGGER = LoggerFactory.getLogger("Graphics");

    private static boolean initialized;
    private static boolean terminated;

    private static Screen primaryScreen;
    private static Map<Long, Screen> handleToScreen;
    private static Map<String, Screen> nameToScreen;

    private static ObservableList<Window> showingWindows = ObservableCollections.observableList(new ArrayList<>());
    private static ObservableList<Window> unmodifiableShowingWindows = ObservableCollections.unmodifiableObservableList(showingWindows);

    private static final ThreadLocal<Long> contextCurrent = ThreadLocal.withInitial(() -> MemoryUtil.NULL);

    public static Screen getPrimaryScreen() {
        return primaryScreen;
    }

    public static Collection<Screen> getScreens() {
        return handleToScreen.values();
    }

    public static Screen getScreen(long handle) {
        return handleToScreen.get(handle);
    }

    public static Screen getScreen(String name) {
        return nameToScreen.get(name);
    }

    public static Screen getScreen(double x, double y) {
        for (Screen screen : getScreens()) {
            VideoMode videoMode = screen.getVideoMode();
            int screenMinX = screen.getWorkareaX();
            int screenMinY = screen.getWorkareaY();
            int screenMaxX = screenMinX + videoMode.getWidth();
            int screenMaxY = screenMinY + videoMode.getHeight();
            if (x >= screenMinX && x < screenMaxX && y >= screenMinY && y < screenMaxY) {
                return screen;
            }
        }
        return primaryScreen;
    }

    public static synchronized void initialize() {
        if (initialized) return;
        initialized = true;
        LOGGER.info("Initializing GLFW context!");
        GLFWErrorCallback.createThrow().set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        initScreens();
    }

    public static synchronized void terminate() {
        if (terminated) return;
        terminated = true;
        glfwTerminate();
    }

    private static void initScreens() {
        PointerBuffer monitorHandles = GLFW.glfwGetMonitors();
        Map<Long, Screen> handleToScreen = new HashMap<>();
        Map<String, Screen> nameToScreen = new HashMap<>();
        for (int i = 0; i < monitorHandles.capacity(); i++) {
            long handle = monitorHandles.get();
            Screen screen = new GLFWScreen(handle);
            handleToScreen.put(handle, screen);
            nameToScreen.put(screen.getName(), screen);
        }
        GLFWContext.handleToScreen = Map.copyOf(handleToScreen);
        GLFWContext.nameToScreen = Map.copyOf(nameToScreen);
        GLFWContext.primaryScreen = getScreen(GLFW.glfwGetPrimaryMonitor());
    }

    public static ObservableList<Window> getShowingWindows() {
        return unmodifiableShowingWindows;
    }

    static void onShowWindow(GLFWWindow window) {
        showingWindows.add(window);
    }

    static void onHideWindow(GLFWWindow window) {
        showingWindows.remove(window);
    }

    static void refreshScale(Screen screen) {
        ((GLFWScreen) screen).refreshScale();
    }

    static void makeContextCurrent(long handle) {
        if (contextCurrent.get() != handle) {
            contextCurrent.set(handle);
            glfwMakeContextCurrent(handle);
        }
    }

    private GLFWContext() {
    }
}

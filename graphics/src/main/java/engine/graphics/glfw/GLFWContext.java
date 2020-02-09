package engine.graphics.glfw;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import engine.graphics.display.Screen;
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

    private static Screen primaryScreen;
    private static Map<Long, Screen> pointerToScreen;
    private static Map<String, Screen> nameToScreen;

    private static ObservableList<Window> showingWindows = ObservableCollections.observableList(new ArrayList<>());
    private static ObservableList<Window> unmodifiableShowingWindows = ObservableCollections.unmodifiableObservableList(showingWindows);

    public static Screen getPrimaryScreen() {
        return primaryScreen;
    }

    public static Collection<Screen> getNameToScreen() {
        return pointerToScreen.values();
    }

    public static Screen getScreen(long pointer) {
        return pointerToScreen.get(pointer);
    }

    public static Screen getScreen(String name) {
        return nameToScreen.get(name);
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
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        Map<Long, Screen> pointerToScreen = new HashMap<>();
        Map<String, Screen> nameToScreen = new HashMap<>();
        for (int i = 0; i < pointerBuffer.capacity(); i++) {
            long pointer = pointerBuffer.get();
            Screen screen = createScreen(pointer);
            pointerToScreen.put(pointer, screen);
            nameToScreen.put(screen.getName(), screen);
        }
        GLFWContext.pointerToScreen = Map.copyOf(pointerToScreen);
        GLFWContext.nameToScreen = Map.copyOf(nameToScreen);
        GLFWContext.primaryScreen = getScreen(GLFW.glfwGetPrimaryMonitor());
    }

    private static Screen createScreen(long pointer) {
        var screen = new GLFWScreen(pointer);
        screen.refresh();
        return screen;
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

    static void refreshScreen(Screen screen) {
        ((GLFWScreen) screen).refresh();
    }

    private GLFWContext() {
    }
}

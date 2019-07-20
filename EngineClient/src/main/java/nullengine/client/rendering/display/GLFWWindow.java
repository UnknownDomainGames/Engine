package nullengine.client.rendering.display;

import nullengine.Platform;
import nullengine.util.RuntimeEnvironment;
import org.apache.commons.lang3.SystemUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    private long pointer;

    private int windowWidth;
    private int windowHeight;
    private int fboWidth;
    private int fboHeight;

    private float contentScaleX;
    private float contentScaleY;

    private boolean resized = false;
    private Matrix4f projection;

    private String title;

    private boolean closed = false;
    private boolean visible = false;
    private DisplayMode displayMode = DisplayMode.WINDOWED;

    private Cursor cursor;

    private final List<KeyCallback> keyCallbacks = new LinkedList<>();
    private final List<MouseCallback> mouseCallbacks = new LinkedList<>();
    private final List<CursorCallback> cursorCallbacks = new LinkedList<>();
    private final List<ScrollCallback> scrollCallbacks = new LinkedList<>();
    private final List<CharCallback> charCallbacks = new LinkedList<>();
    private final List<WindowCloseCallback> windowCloseCallbacks = new LinkedList<>();
    private final List<CursorEnterCallback> cursorEnterCallbacks = new LinkedList<>();

    public GLFWWindow(int width, int height, String title) {
        this.title = title;
        this.windowWidth = width;
        this.windowHeight = height;
    }

    @Override
    public int getWidth() {
        return fboWidth;
    }

    @Override
    public int getHeight() {
        return fboHeight;
    }

    @Override
    public float getContentScaleX() {
        return contentScaleX;
    }

    @Override
    public float getContentScaleY() {
        return contentScaleY;
    }

    @Override
    public void setSize(int width, int height) {
        this.fboWidth = width;
        this.fboHeight = height;
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer f1 = memoryStack.mallocFloat(1);
            FloatBuffer f2 = memoryStack.mallocFloat(1);

            glfwGetWindowContentScale(pointer, f1, f2);

            if (contentScaleX != f1.get(0) || contentScaleY != f2.get(0)) {
                contentScaleX = f1.get(0);
                contentScaleY = f2.get(0);
            }
        }
        this.windowWidth = Math.round(width / contentScaleX);
        this.windowHeight = Math.round(height / contentScaleX);
        this.resized = true;
        glViewport(0, 0, width, height);
    }

    @Override
    public Matrix4fc projection() {
        if (resized || projection == null) {
            projection = new Matrix4f().perspective((float) (Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f)))), windowWidth / (float) windowHeight, 0.01f, 1000f);
        }
        return projection;
    }

    @Override
    public boolean isResized() {
        return resized;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        glfwSetWindowTitle(pointer, title);
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void addKeyCallback(KeyCallback callback) {
        keyCallbacks.add(requireNonNull(callback));
    }

    @Override
    public void removeKeyCallback(KeyCallback callback) {
        keyCallbacks.remove(callback);
    }

    @Override
    public void addMouseCallback(MouseCallback callback) {
        mouseCallbacks.add(requireNonNull(callback));
    }

    @Override
    public void removeMouseCallback(MouseCallback callback) {
        mouseCallbacks.remove(callback);
    }

    @Override
    public void addCursorCallback(CursorCallback callback) {
        cursorCallbacks.add(requireNonNull(callback));
    }

    @Override
    public void removeCursorCallback(CursorCallback callback) {
        cursorCallbacks.remove(callback);
    }

    @Override
    public void addScrollCallback(ScrollCallback callback) {
        scrollCallbacks.add(requireNonNull(callback));
    }

    @Override
    public void removeScrollCallback(ScrollCallback callback) {
        scrollCallbacks.remove(callback);
    }

    @Override
    public void addCharCallback(CharCallback callback) {
        charCallbacks.add(requireNonNull(callback));
    }

    @Override
    public void removeCharCallback(CharCallback callback) {
        charCallbacks.remove(callback);
    }

    @Override
    public void addWindowCloseCallback(WindowCloseCallback callback) {
        windowCloseCallbacks.add(callback);
    }

    @Override
    public void removeWindowCloseCallback(WindowCloseCallback callback) {
        windowCloseCallbacks.remove(callback);
    }

    @Override
    public void addCursorEnterCallback(CursorEnterCallback callback) {
        cursorEnterCallbacks.add(callback);
    }

    @Override
    public void removeCursorEnterCallback(CursorEnterCallback callback) {
        cursorEnterCallbacks.remove(callback);
    }

    @Override
    public void beginRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void endRender() {
        glfwSwapBuffers(pointer);

        if (isResized()) {
            resized = false;
        }

        glfwPollEvents();
    }

    @Override
    public void close() {
        closed = true;
        glfwDestroyWindow(pointer);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void show() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible == visible)
            return;
        this.visible = visible;
        if (visible)
            glfwShowWindow(pointer);
        else
            glfwHideWindow(pointer);
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void dispose() {
        hide();

        glfwDestroyWindow(pointer);
    }

    public long getPointer() {
        return pointer;
    }

    public void init() {
        initErrorCallback(System.err);
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        initWindowHint();
        pointer = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);
        if (!checkCreated())
            throw new RuntimeException("Failed to parse the GLFW window");
        long monitor = glfwGetPrimaryMonitor();
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer f1 = memoryStack.mallocFloat(1);
            FloatBuffer f2 = memoryStack.mallocFloat(1);
            glfwGetMonitorContentScale(monitor, f1, f2);
            contentScaleX = f1.get(0);
            contentScaleY = f2.get(0);
        }
        initCallbacks();
        setWindowPosCenter();
        glfwMakeContextCurrent(pointer);
        enableVSync();
        cursor = new GLFWCursor(pointer);
        setupInput();
    }

    @Override
    public DisplayMode getDisplayMode(){
        return displayMode;
    }

    private int lastPosX, lastPosY, lastWidth, lastHeight;

    @Override
    public void setDisplayMode(DisplayMode displayMode) {
        if (this.displayMode == displayMode) return;
        var mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        switch (displayMode) {
            case FULLSCREEN:
                if(this.displayMode == DisplayMode.WINDOWED){
                    int[] a = new int[1], b = new int[1];
                    glfwGetWindowPos(pointer, a, b);
                    lastPosX = a[0];
                    lastPosY = b[0];
                    glfwGetWindowSize(pointer, a, b);
                    lastWidth = a[0];
                    lastHeight = b[0];
                }
                glfwSetWindowMonitor(pointer, getCurrentMonitor(), 0, 0, mode.width(), mode.height(), mode.refreshRate());
                break;
            case WINDOWED_FULLSCREEN:
                if(this.displayMode == DisplayMode.WINDOWED){
                    int[] a = new int[1], b = new int[1];
                    glfwGetWindowPos(pointer, a, b);
                    lastPosX = a[0];
                    lastPosY = b[0];
                    glfwGetWindowSize(pointer, a, b);
                    lastWidth = a[0];
                    lastHeight = b[0];
                }
                glfwSetWindowAttrib(pointer, GLFW_DECORATED, GL_FALSE);
                glfwSetWindowMonitor(pointer, NULL, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                break;
            case WINDOWED:
                glfwSetWindowAttrib(pointer, GLFW_DECORATED, GL_TRUE);
                glfwSetWindowMonitor(pointer, NULL, lastPosX, lastPosY, lastWidth, lastHeight, mode.refreshRate());
        }
        this.displayMode = displayMode;
    }

    private void setupInput() {
        glfwSetKeyCallback(pointer, (window, key, scancode, action, mods) -> keyCallbacks.forEach(callback -> callback.invoke(this, key, scancode, action, mods)));
        glfwSetMouseButtonCallback(pointer, (window, button, action, mods) -> mouseCallbacks.forEach(callback -> callback.invoke(this, button, action, mods)));
        glfwSetCursorPosCallback(pointer, (window, xpos, ypos) -> cursorCallbacks.forEach(callback -> callback.invoke(this, xpos, ypos)));
        glfwSetScrollCallback(pointer, (window, xoffset, yoffset) -> scrollCallbacks.forEach(callback -> callback.invoke(this, xoffset, yoffset)));
        glfwSetCharCallback(pointer, (window, codepoint) -> charCallbacks.forEach(callback -> callback.invoke(this, (char) codepoint)));
        glfwSetWindowCloseCallback(pointer, window -> windowCloseCallbacks.forEach(callback -> callback.invoke(this)));
        glfwSetCursorEnterCallback(pointer, (window, entered) -> cursorEnterCallbacks.forEach(callback -> callback.invoke(this, entered)));
    }

    private boolean checkCreated() {
        return pointer != NULL;
    }

    private void initCallbacks() {
        glfwSetFramebufferSizeCallback(pointer, (window, width, height) -> setSize(width, height));
    }

    private void initWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GL_TRUE);
        if (Platform.getEngineClient().getRuntimeEnvironment() != RuntimeEnvironment.DEPLOYMENT) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        }
        if (SystemUtils.IS_OS_MAC) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }
    }

    private void initErrorCallback(PrintStream stream) {
        GLFWErrorCallback.createPrint(stream).set();
    }

    private void setWindowPosCenter() {
        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        // Center our window
        glfwSetWindowPos(pointer, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
    }

    private void enableVSync() {
        glfwSwapInterval(1);
    }

    public long getCurrentMonitor(){
        int[] a = new int[1], b = new int[1];
        org.lwjgl.glfw.GLFW.glfwGetWindowPos(getPointer(), a, b);
        var wx = a[0];
        var wy = b[0];
        org.lwjgl.glfw.GLFW.glfwGetWindowSize(getPointer(), a, b);
        var ww = a[0];
        var wh = b[0];
        var monitors = glfwGetMonitors();
        var bestOverlap = 0;
        var bestMonitor = NULL;
        for(int i = 0; i < monitors.limit(); i++){
            var mode = glfwGetVideoMode(monitors.get(i));
            glfwGetMonitorPos(monitors.get(i), a,b);
            var mx = a[0];
            var my = b[0];
            var mw = mode.width();
            var mh = mode.height();
            var overlap = Math.max(0, Math.min(wx + ww, mx + mw) - Math.max(wx, mx)) *
                            Math.max(0, Math.min(wy + wh, my + mh) - Math.max(wy, my));
            if(bestOverlap < overlap){
                bestOverlap = overlap;
                bestMonitor = monitors.get(i);
            }
        }
        return bestMonitor;
    }
}

package nullengine.client.rendering.glfw;

import nullengine.client.rendering.display.*;
import org.apache.commons.lang3.SystemUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    private long pointer;

    private int posX;
    private int posY;

    private int windowWidth;
    private int windowHeight;
    private int fboWidth;
    private int fboHeight;

    private Monitor monitor;

    private boolean resized = false;
    private Matrix4f projection;

    private String title;

    private boolean closed = false;
    private boolean visible = false;
    private boolean decorated = true;
    private boolean resizable = true;
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
        this.fboWidth = width;
        this.fboHeight = height;
    }

    @Override
    public int getX() {
        return posX;
    }

    @Override
    public int getY() {
        return posY;
    }

    @Override
    public void setPos(int x, int y) {
        posX = x;
        posY = y;
        glfwSetWindowPos(pointer, x, y);
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
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        if (this.monitor == monitor) return;
        this.monitor = monitor;
        resize(fboWidth, fboHeight);
    }

    @Override
    public float getContentScaleX() {
        return monitor.getScaleX();
    }

    @Override
    public float getContentScaleY() {
        return monitor.getScaleY();
    }

    @Override
    public void setSize(int width, int height) {
        resize();
    }

    protected void resize() {
        resize(fboWidth, fboHeight);
    }

    protected void resize(int width, int height) {
        resized = true;
        fboWidth = width;
        fboHeight = height;
        windowWidth = Math.round(width / getContentScaleX());
        windowHeight = Math.round(height / getContentScaleY());
    }

    @Override
    public Matrix4fc projection() {
        if (resized || projection == null)
            projection = new Matrix4f().perspective((float) Math.toRadians(60), (float) fboWidth / fboHeight, 0.01f, 1000f);
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
        this.title = title;
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
    public void swapBufferAndPollEvents() {
        glfwSwapBuffers(pointer);

        if (resized) {
            resized = false;
        }

        glfwPollEvents();
    }

    @Override
    public void close() {
        if (closed) return;
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
        return visible;
    }

    @Override
    public boolean isDecorated() {
        return decorated;
    }

    @Override
    public void setDecorated(boolean decorated) {
        if (this.decorated == decorated) {
            return;
        }
        this.decorated = decorated;
        glfwSetWindowAttrib(pointer, GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);
    }

    @Override
    public boolean isResizable() {
        return resizable;
    }

    @Override
    public void setResizable(boolean resizable) {
        if (this.resizable == resizable) {
            return;
        }
        this.resizable = resizable;
        glfwSetWindowAttrib(pointer, GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
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
        setMonitor(DisplayInfo.instance().getPrimaryMonitor());
        initWindowHint();
        pointer = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);
        if (!checkCreated()) {
            throw new RuntimeException("Failed to parse the GLFW window");
        }
        initCallbacks();
        setWindowPosCenter();
        glfwMakeContextCurrent(pointer);
        enableVSync();
        cursor = new GLFWCursor(pointer);
        setupInput();
        resize();
    }

    @Override
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    private int lastPosX, lastPosY, lastWidth, lastHeight;

    @Override
    public void setDisplayMode(DisplayMode displayMode, int newWidth, int newHeight, int frameRate) {
        if (this.displayMode == displayMode && newWidth == -1 && newHeight == -1) return;
        var nw = newWidth != -1 ? newWidth : monitor.getVideoMode().getWidth();
        var nh = newHeight != -1 ? newHeight : monitor.getVideoMode().getHeight();
        switch (displayMode) {
            case FULLSCREEN:
                if (this.displayMode == DisplayMode.WINDOWED) {
                    lastPosX = posX;
                    lastPosY = posY;
                    lastWidth = fboWidth;
                    lastHeight = fboHeight;
                }
                glfwSetWindowMonitor(pointer, monitor.getPointer(), 0, 0, nw, nh, frameRate > 0 ? frameRate : monitor.getVideoMode().getRefreshRate());
                break;
            case WINDOWED_FULLSCREEN:
                if (this.displayMode == DisplayMode.WINDOWED) {
                    lastPosX = posX;
                    lastPosY = posY;
                    lastWidth = fboWidth;
                    lastHeight = fboHeight;
                }
                setDecorated(false);
                glfwSetWindowMonitor(pointer, NULL, 0, 0, monitor.getVideoMode().getWidth(), monitor.getVideoMode().getHeight(), monitor.getVideoMode().getRefreshRate());
                break;
            case WINDOWED:
                setDecorated(true);
                glfwSetWindowMonitor(pointer, NULL, lastPosX, lastPosY, newWidth != -1 ? newWidth : lastWidth, newHeight != -1 ? newHeight : lastHeight, monitor.getVideoMode().getRefreshRate());
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
        glfwSetWindowPosCallback(pointer, (window, xpos, ypos) -> {
            posX = xpos;
            posY = ypos;
        });
        glfwSetFramebufferSizeCallback(pointer, (window, width, height) -> resize(width, height));
    }

    private void initWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        if (Boolean.parseBoolean(System.getProperty("glfw.debug", "false"))) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        }
        if (SystemUtils.IS_OS_MAC) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }
    }

    private void setWindowPosCenter() {
        setPos((monitor.getVideoMode().getWidth() - windowWidth) / 2, (monitor.getVideoMode().getHeight() - windowHeight) / 2);
    }

    private void enableVSync() {
        glfwSwapInterval(1);
    }
}

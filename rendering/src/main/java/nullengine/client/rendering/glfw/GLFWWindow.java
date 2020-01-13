package nullengine.client.rendering.glfw;

import nullengine.client.rendering.display.Cursor;
import nullengine.client.rendering.display.DisplayMode;
import nullengine.client.rendering.display.Monitor;
import nullengine.client.rendering.display.Window;
import nullengine.client.rendering.display.callback.*;
import nullengine.client.rendering.image.ReadOnlyImage;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    private long pointer;

    private int x;
    private int y;

    private int width;
    private int height;

    private Monitor monitor;

    private boolean resized = false;

    private String title;

    private boolean showing = false;

    private DisplayMode displayMode = DisplayMode.WINDOWED;

    // Window attributes
    private boolean focused = false;
    private boolean decorated = true;
    private boolean resizable = true;
    private boolean floating = false;
    private boolean transparent = false;
    private boolean iconified = false;
    private boolean maximized = false;

    private Cursor cursor;

    private final List<KeyCallback> keyCallbacks = new LinkedList<>();
    private final List<MouseCallback> mouseCallbacks = new LinkedList<>();
    private final List<CursorCallback> cursorCallbacks = new LinkedList<>();
    private final List<ScrollCallback> scrollCallbacks = new LinkedList<>();
    private final List<CharCallback> charCallbacks = new LinkedList<>();
    private final List<CharModsCallback> charModsCallbacks = new LinkedList<>();
    private final List<WindowCloseCallback> windowCloseCallbacks = new LinkedList<>();
    private final List<WindowFocusCallback> windowFocusCallbacks = new LinkedList<>();
    private final List<WindowIconifyCallback> windowIconifyCallbacks = new LinkedList<>();
    private final List<WindowMaximizeCallback> windowMaximizeCallbacks = new LinkedList<>();
    private final List<CursorEnterCallback> cursorEnterCallbacks = new LinkedList<>();
    private final List<WindowSizeCallback> windowSizeCallbacks = new LinkedList<>();
    private final List<WindowPosCallback> windowPosCallbacks = new LinkedList<>();
    private final List<DropCallback> dropCallbacks = new LinkedList<>();

    public GLFWWindow() {
        this("");
    }

    public GLFWWindow(String title) {
        this(854, 480, title);
    }

    public GLFWWindow(int width, int height, String title) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setPos(int x, int y) {
        glfwSetWindowPos(pointer, x, y);
        setPotInternal(x, y);
    }

    private void setPotInternal(int x, int y) {
        this.x = x;
        this.y = y;
        windowPosCallbacks.forEach(callback -> callback.invoke(this, x, y));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        if (this.monitor == monitor) return;
        this.monitor = monitor;
        resize();
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
        resize(width, height);
    }

    private void resize() {
        resize(width, height);
    }

    protected void resize(int width, int height) {
        resized = true;
        this.width = width;
        this.height = height;
        windowSizeCallbacks.forEach(callback -> callback.invoke(this, width, height));
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
    public void setIcon(ReadOnlyImage... icons) {
        var glfwImages = GLFWImage.create(icons.length);
        for (int i = 0; i < icons.length; i++) {
            glfwImages.get(i).set(icons[i].getWidth(), icons[i].getHeight(), icons[i].getPixelBuffer());
        }
        glfwSetWindowIcon(pointer, glfwImages);
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(pointer);

        if (resized) {
            resized = false;
        }
    }

    @Override
    public void show() {
        if (pointer == NULL) init();

        setShowing(true);
    }

    @Override
    public void hide() {
        setShowing(false);
    }

    private void setShowing(boolean showing) {
        if (this.showing == showing)
            return;
        this.showing = showing;
        if (showing) {
            glfwShowWindow(pointer);
            GLFWContext.onShowedWindow(this);
        } else {
            glfwHideWindow(pointer);
            GLFWContext.onHidedWindow(this);
        }
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void dispose() {
        disposeInternal();
        GLFWContext.onDisposedWindow(this);
    }

    void disposeInternal() {
        if (pointer == NULL) return;

        hide();
        glfwDestroyWindow(pointer);
        pointer = NULL;
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
                    lastPosX = x;
                    lastPosY = y;
                    lastWidth = width;
                    lastHeight = height;
                }
                glfwSetWindowMonitor(pointer, monitor.getPointer(), 0, 0, nw, nh, frameRate > 0 ? frameRate : monitor.getVideoMode().getRefreshRate());
                break;
            case WINDOWED_FULLSCREEN:
                if (this.displayMode == DisplayMode.WINDOWED) {
                    lastPosX = x;
                    lastPosY = y;
                    lastWidth = width;
                    lastHeight = height;
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

    public long getPointer() {
        return pointer;
    }

    public void init() {
        setMonitor(GLFWContext.getPrimaryMonitor());
        initWindowHint();
        pointer = glfwCreateWindow(width, height, title, NULL, NULL);
        checkCreated();
        width *= getContentScaleX();
        height *= getContentScaleY(); // pre-scale it to prevent weird behavior of Gui caused by missed call of resize()
        initCallbacks();
        setWindowPosCenter();
        glfwMakeContextCurrent(pointer);
        enableVSync();
        cursor = new GLFWCursor(pointer);
        resize();
        GLFWContext.onInitializedWindow(this);
    }

    private void checkCreated() {
        if (pointer == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

    private void initWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        if (Boolean.parseBoolean(System.getProperty("rendering.debug", "false"))) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        }
        if (SystemUtils.IS_OS_MAC) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }
    }

    private void setWindowPosCenter() {
        setPos((monitor.getVideoMode().getWidth() - width) / 2, (monitor.getVideoMode().getHeight() - height) / 2);
    }

    private void enableVSync() {
        glfwSwapInterval(1);
    }

    // ================= Window Attributes Start =================
    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void focus() {
        glfwFocusWindow(pointer);
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
    public boolean isFloating() {
        return floating;
    }

    @Override
    public void setFloating(boolean floating) {
        if (this.floating == floating) {
            return;
        }
        this.floating = floating;
        glfwSetWindowAttrib(pointer, GLFW_FLOATING, floating ? GLFW_TRUE : GLFW_FALSE);
    }

    @Override
    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public void setTransparent(boolean transparent) {
        if (this.transparent == transparent) {
            return;
        }
        this.transparent = transparent;
        glfwSetWindowAttrib(pointer, GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW_TRUE : GLFW_FALSE);
    }

    @Override
    public boolean isIconified() {
        return iconified;
    }

    @Override
    public boolean isMaximized() {
        return maximized;
    }

    @Override
    public void iconify() {
        glfwIconifyWindow(pointer);
    }

    @Override
    public void maximize() {
        glfwMaximizeWindow(pointer);
    }

    @Override
    public void restore() {
        glfwRestoreWindow(pointer);
    }

    // ================= Window Attributes End =================

    // ================= Window Callbacks Start =================
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
    public void addCharModsCallback(CharModsCallback callback) {
        charModsCallbacks.add(callback);
    }

    @Override
    public void removeCharModsCallback(CharModsCallback callback) {
        charModsCallbacks.remove(callback);
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
    public void addWindowFocusCallback(WindowFocusCallback callback) {
        windowFocusCallbacks.add(callback);
    }

    @Override
    public void removeWindowFocusCallback(WindowFocusCallback callback) {
        windowFocusCallbacks.remove(callback);
    }

    @Override
    public void addWindowIconifyCallback(WindowIconifyCallback callback) {
        windowIconifyCallbacks.add(callback);
    }

    @Override
    public void removeWindowIconifyCallback(WindowIconifyCallback callback) {
        windowIconifyCallbacks.remove(callback);
    }

    @Override
    public void addWindowMaximizeCallback(WindowMaximizeCallback callback) {
        windowMaximizeCallbacks.add(callback);
    }

    @Override
    public void removeWindowMaximizeCallback(WindowMaximizeCallback callback) {
        windowMaximizeCallbacks.remove(callback);
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
    public void addWindowSizeCallback(WindowSizeCallback callback) {
        windowSizeCallbacks.add(callback);
    }

    @Override
    public void removeWindowSizeCallback(WindowSizeCallback callback) {
        windowSizeCallbacks.remove(callback);
    }

    @Override
    public void addWindowPosCallback(WindowPosCallback callback) {
        windowPosCallbacks.add(callback);
    }

    @Override
    public void removeWindowPosCallback(WindowPosCallback callback) {
        windowPosCallbacks.remove(callback);
    }

    @Override
    public void addDropCallback(DropCallback callback) {
        dropCallbacks.add(callback);
    }

    @Override
    public void removeDropCallback(DropCallback callback) {
        dropCallbacks.remove(callback);
    }

    private void initCallbacks() {
        glfwSetKeyCallback(pointer, (window, key, scancode, action, mods) ->
                keyCallbacks.forEach(callback -> callback.invoke(this, key, scancode, action, mods)));
        glfwSetMouseButtonCallback(pointer, (window, button, action, mods) ->
                mouseCallbacks.forEach(callback -> callback.invoke(this, button, action, mods)));
        glfwSetCursorPosCallback(pointer, (window, xpos, ypos) ->
                cursorCallbacks.forEach(callback -> callback.invoke(this, xpos, ypos)));
        glfwSetScrollCallback(pointer, (window, xoffset, yoffset) ->
                scrollCallbacks.forEach(callback -> callback.invoke(this, xoffset, yoffset)));
        glfwSetCharCallback(pointer, (window, codepoint) ->
                charCallbacks.forEach(callback -> callback.invoke(this, codepoint)));
        glfwSetCharModsCallback(pointer, (window, codepoint, mods) ->
                charModsCallbacks.forEach(callback -> callback.invoke(this, codepoint, mods)));
        glfwSetWindowCloseCallback(pointer, window -> {
            windowCloseCallbacks.forEach(callback -> callback.invoke(this));
            dispose();
        });
        glfwSetWindowFocusCallback(pointer, (window, focused) -> {
            this.focused = focused;
            windowFocusCallbacks.forEach(callback -> callback.invoke(this, focused));
        });
        glfwSetCursorEnterCallback(pointer, (window, entered) ->
                cursorEnterCallbacks.forEach(callback -> callback.invoke(this, entered)));
        glfwSetWindowPosCallback(pointer, (window, xpos, ypos) -> setPotInternal(xpos, ypos));
        glfwSetWindowSizeCallback(pointer, (window, width, height) -> resize(width, height));
        glfwSetWindowContentScaleCallback(pointer, ((window, xscale, yscale) -> monitor.refreshMonitor()));
        glfwSetDropCallback(pointer, (window, count, names) -> {
            String[] files = new String[count];
            PointerBuffer buffer = MemoryUtil.memPointerBuffer(names, count);
            for (int i = 0; i < count; i++) {
                files[i] = MemoryUtil.memUTF8(buffer.get());
            }
            dropCallbacks.forEach(callback -> callback.invoke(this, files));
        });
        glfwSetWindowIconifyCallback(pointer, (window, iconified) -> {
            this.iconified = iconified;
            windowIconifyCallbacks.forEach(callback -> callback.invoke(this, iconified));
        });
        glfwSetWindowMaximizeCallback(pointer, (window, maximized) -> {
            this.maximized = maximized;
            windowMaximizeCallbacks.forEach(callback -> callback.invoke(this, maximized));
        });

        // TODO: callbacks
//        glfwSetWindowRefreshCallback()
//        glfwSetWindowContentScaleCallback()
    }
    // ================= Window Callbacks End =================
}

package engine.graphics.glfw;

import com.google.common.base.Strings;
import engine.graphics.GraphicsEngine;
import engine.graphics.display.Cursor;
import engine.graphics.display.DisplayMode;
import engine.graphics.display.Screen;
import engine.graphics.display.Window;
import engine.graphics.display.callback.*;
import engine.graphics.image.ReadOnlyImage;
import engine.graphics.util.Cleaner;
import engine.input.Action;
import engine.input.KeyCode;
import engine.input.Modifiers;
import engine.input.MouseButton;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    protected long pointer;
    protected Cleaner.Disposable disposable;

    private final GLFWWindow parent;

    protected int x;
    protected int y;

    protected int width;
    protected int height;

    private Screen screen;

    private boolean resized = false;

    protected String title = "";

    private boolean showing = false;
    private boolean doCloseImmediately = true;

    private DisplayMode displayMode = DisplayMode.WINDOWED;

    // Window attributes
    private boolean focused = false;
    private boolean decorated = true;
    private boolean resizable = true;
    private boolean floating = false;
    private boolean transparent = false;
    private boolean iconified = false;
    private boolean maximized = false;

    protected Cursor cursor;

    private final List<KeyCallback> keyCallbacks = new LinkedList<>();
    private final List<MouseCallback> mouseCallbacks = new LinkedList<>();
    private final List<CursorCallback> cursorCallbacks = new LinkedList<>();
    private final List<ScrollCallback> scrollCallbacks = new LinkedList<>();
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
        this(null);
    }

    public GLFWWindow(Window parent) {
        this(parent, 854, 480);
    }

    public GLFWWindow(int width, int height) {
        this(null, width, height);
    }

    public GLFWWindow(Window parent, int width, int height) {
        if (!(parent instanceof GLFWWindow || parent == null))
            throw new IllegalArgumentException("Parent window is not GLFWWindow or null");
        this.parent = (GLFWWindow) parent;
        this.width = width;
        this.height = height;
    }

    @Override
    public Window getParent() {
        return parent;
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
        setPosInternal(x, y);
    }

    private void setPosInternal(int x, int y) {
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
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void setScreen(Screen screen) {
        if (this.screen == screen) return;
        this.screen = screen;
        resize();
    }

    @Override
    public float getContentScaleX() {
        return screen.getScaleX();
    }

    @Override
    public float getContentScaleY() {
        return screen.getScaleY();
    }

    @Override
    public void setSize(int width, int height) {
        resize(width, height);
    }

    protected void resize() {
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
        this.title = Strings.nullToEmpty(title);
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
    public boolean isDoCloseImmediately() {
        return doCloseImmediately;
    }

    @Override
    public void setDoCloseImmediately(boolean doCloseImmediately) {
        this.doCloseImmediately = doCloseImmediately;
    }

    @Override
    public void dispose() {
        if (pointer == NULL) return;

        hide();
        disposable.dispose();
        pointer = NULL;
    }

    @Override
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    private int lastPosX, lastPosY, lastWidth, lastHeight;

    @Override
    public void setDisplayMode(DisplayMode displayMode, int width, int height, int frameRate) {
        if (this.displayMode == displayMode && width == -1 && height == -1) return;
        var nw = width != -1 ? width : screen.getVideoMode().getWidth();
        var nh = height != -1 ? height : screen.getVideoMode().getHeight();
        switch (displayMode) {
            case FULLSCREEN:
                if (this.displayMode == DisplayMode.WINDOWED) {
                    lastPosX = x;
                    lastPosY = y;
                    lastWidth = this.width;
                    lastHeight = this.height;
                }
                glfwSetWindowMonitor(pointer, screen.getPointer(), 0, 0, nw, nh, frameRate > 0 ? frameRate : screen.getVideoMode().getRefreshRate());
                break;
            case WINDOWED_FULLSCREEN:
                if (this.displayMode == DisplayMode.WINDOWED) {
                    lastPosX = x;
                    lastPosY = y;
                    lastWidth = this.width;
                    lastHeight = this.height;
                }
                setDecorated(false);
                glfwSetWindowMonitor(pointer, NULL, 0, 0, screen.getVideoMode().getWidth(), screen.getVideoMode().getHeight(), screen.getVideoMode().getRefreshRate());
                break;
            case WINDOWED:
                setDecorated(true);
                glfwSetWindowMonitor(pointer, NULL, lastPosX, lastPosY, width != -1 ? width : lastWidth, height != -1 ? height : lastHeight, screen.getVideoMode().getRefreshRate());
        }
        this.displayMode = displayMode;
    }

    public long getPointer() {
        return pointer;
    }

    public void init() {
        setScreen(GLFWContext.getPrimaryScreen());
        initWindowHint();
        pointer = glfwCreateWindow(width, height, title, NULL, parent == null ? NULL : getRootWindow().getPointer());
        checkCreated();
        disposable = createDisposable(pointer);
        width *= getContentScaleX();
        height *= getContentScaleY(); // pre-scale it to prevent weird behavior of Gui caused by missed call of resize()
        initCallbacks();
        setWindowPosCenter();
        glfwMakeContextCurrent(pointer);
        enableVSync();
        cursor = new GLFWCursor(pointer);
        resize();
    }

    protected GLFWWindow getRootWindow() {
        GLFWWindow window = this.parent;
        while (window.parent != null) {
            window = window.parent;
        }
        return window;
    }

    protected Cleaner.Disposable createDisposable(long pointer) {
        return Cleaner.register(this, () -> glfwDestroyWindow(pointer));
    }

    protected void checkCreated() {
        if (pointer == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

    protected void initWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        if (GraphicsEngine.isDebug()) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        }
        if (SystemUtils.IS_OS_MAC) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }
    }

    protected void setWindowPosCenter() {
        setPos((screen.getVideoMode().getWidth() - width) / 2, (screen.getVideoMode().getHeight() - height) / 2);
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

    protected void initCallbacks() {
        glfwSetKeyCallback(pointer, (window, key, scancode, action, mods) -> {
            KeyCode _key = KeyCode.valueOf(key);
            Action _action = Action.values()[action];
            Modifiers modifiers = Modifiers.of(mods);
            keyCallbacks.forEach(callback -> callback.invoke(this, _key, scancode, _action, modifiers));
        });
        glfwSetMouseButtonCallback(pointer, (window, button, action, mods) -> {
            MouseButton _button = MouseButton.valueOf(button);
            Action _action = Action.values()[action];
            Modifiers modifiers = Modifiers.of(mods);
            mouseCallbacks.forEach(callback -> callback.invoke(this, _button, _action, modifiers));
        });
        glfwSetCursorPosCallback(pointer, (window, xpos, ypos) ->
                cursorCallbacks.forEach(callback -> callback.invoke(this, xpos, ypos)));
        glfwSetScrollCallback(pointer, (window, xoffset, yoffset) ->
                scrollCallbacks.forEach(callback -> callback.invoke(this, xoffset, yoffset)));
        glfwSetCharModsCallback(pointer, (window, codepoint, mods) -> {
            Modifiers modifiers = Modifiers.of(mods);
            charModsCallbacks.forEach(callback -> callback.invoke(this, (char) codepoint, modifiers));
        });
        glfwSetWindowCloseCallback(pointer, window -> {
            new ArrayList<>(windowCloseCallbacks).forEach(callback -> callback.invoke(this));
            if (doCloseImmediately) {
                dispose();
            }
        });
        glfwSetWindowFocusCallback(pointer, (window, focused) -> {
            this.focused = focused;
            windowFocusCallbacks.forEach(callback -> callback.invoke(this, focused));
        });
        glfwSetCursorEnterCallback(pointer, (window, entered) ->
                cursorEnterCallbacks.forEach(callback -> callback.invoke(this, entered)));
        glfwSetWindowPosCallback(pointer, (window, xpos, ypos) -> setPosInternal(xpos, ypos));
        glfwSetWindowSizeCallback(pointer, (window, width, height) -> resize(width, height));
        glfwSetWindowContentScaleCallback(pointer, ((window, xscale, yscale) -> GLFWContext.refreshScreen(screen)));
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

//        Unused window callbacks
//        glfwSetWindowRefreshCallback()
//        glfwSetWindowContentScaleCallback()
    }
    // ================= Window Callbacks End =================
}

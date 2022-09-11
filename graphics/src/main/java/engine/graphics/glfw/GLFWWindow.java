package engine.graphics.glfw;

import com.google.common.base.Strings;
import engine.graphics.GraphicsEngine;
import engine.graphics.display.*;
import engine.graphics.display.callback.*;
import engine.graphics.image.ReadOnlyImage;
import engine.graphics.util.Cleaner;
import engine.input.Action;
import engine.input.KeyCode;
import engine.input.Modifiers;
import engine.input.MouseButton;
import engine.util.LinearGrowthList;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.file.Path;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    private static final int[] GLFW_CURSOR_STATES = new int[]{GLFW_CURSOR_NORMAL, GLFW_CURSOR_HIDDEN, GLFW_CURSOR_DISABLED};
    private static final int[] GLFW_CURSOR_SHAPES = new int[]{0, GLFW_ARROW_CURSOR, GLFW_IBEAM_CURSOR, GLFW_CROSSHAIR_CURSOR, GLFW_HAND_CURSOR, GLFW_HRESIZE_CURSOR, GLFW_VRESIZE_CURSOR};

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

    private CursorState cursorState = CursorState.NORMAL;
    private CursorShape cursorShape = CursorShape.NORMAL;

    private long cursorPointer;

    private final List<KeyCallback> keyCallbacks = new LinearGrowthList<>();
    private final List<MouseCallback> mouseCallbacks = new LinearGrowthList<>();
    private final List<CursorCallback> cursorCallbacks = new LinearGrowthList<>();
    private final List<ScrollCallback> scrollCallbacks = new LinearGrowthList<>();
    private final List<CharModsCallback> charModsCallbacks = new LinearGrowthList<>();
    private final List<WindowCloseCallback> windowCloseCallbacks = new LinearGrowthList<>();
    private final List<WindowFocusCallback> windowFocusCallbacks = new LinearGrowthList<>();
    private final List<WindowIconifyCallback> windowIconifyCallbacks = new LinearGrowthList<>();
    private final List<WindowMaximizeCallback> windowMaximizeCallbacks = new LinearGrowthList<>();
    private final List<CursorEnterCallback> cursorEnterCallbacks = new LinearGrowthList<>();
    private final List<WindowSizeCallback> windowSizeCallbacks = new LinearGrowthList<>();
    private final List<WindowPosCallback> windowPosCallbacks = new LinearGrowthList<>();
    private final List<DropCallback> dropCallbacks = new LinearGrowthList<>();

    public GLFWWindow() {
        this(null);
    }

    public GLFWWindow(Window parent) {
        this(parent, 1, 1);
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

    public long getPointer() {
        return pointer;
    }

    public void init() {
        screen = parent != null ? parent.getScreen() : GLFWContext.getPrimaryScreen();
        var minWindowSize = 1;
        if (SystemUtils.IS_OS_LINUX) {
            // 1 causes "Bad Parameters" on X11
            minWindowSize = 2;
        }
        width = (int) Math.max(width * getContentScaleX(), minWindowSize);
        height = (int) Math.max(height * getContentScaleY(), minWindowSize); // pre-scale it to prevent weird behavior of Gui caused by missed call of resize()
        initWindowHint();
        pointer = glfwCreateWindow(width, height, title, NULL, parent == null ? NULL : getRootWindow().getPointer());
        checkCreated();
        disposable = createDisposable(pointer);
        if (parent == null) glfwMakeContextCurrent(pointer);
        initCallbacks();
        notifyResized();
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
    }

    private void relocate(int x, int y) {
        if (x == -32000 || y == -32000) return; // window iconified.
        this.x = x;
        this.y = y;
        this.screen = GLFWContext.getScreen(x, y);
        windowPosCallbacks.forEach(callback -> callback.invoke(this, x, y));
    }

    @Override
    public void centerOnScreen() {
        setPos((screen.getVideoMode().getWidth() - width) / 2, (screen.getVideoMode().getHeight() - height) / 2);
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
        notifyResized();
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
        glfwSetWindowSize(pointer, width, height);
    }

    protected void notifyResized() {
        resized = true;
        windowSizeCallbacks.forEach(callback -> callback.invoke(this, width, height));
    }

    protected void resize(int width, int height) {
        this.width = width;
        this.height = height;
        notifyResized();
    }

    @Override
    public boolean isResized() {
        return resized;
    }

    @Override
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    private int lastPosX, lastPosY, lastWidth, lastHeight;
    private boolean lastFloating, lastDecorated;

    @Override
    public void setDisplayMode(DisplayMode displayMode, int width, int height, int frameRate) {
        if (this.displayMode == displayMode && width == -1 && height == -1) return;
        if (this.displayMode == DisplayMode.WINDOWED) {
            lastPosX = getX();
            lastPosY = getY();
            lastWidth = getWidth();
            lastHeight = getHeight();
            lastFloating = isFloating();
            lastDecorated = isDecorated();
        }

        VideoMode videoMode = screen.getVideoMode();
        int finalX = 0, finalY = 0;
        int finalWidth = 0, finalHeight = 0;
        switch (displayMode) {
            case FULLSCREEN:
                glfwSetWindowMonitor(pointer, screen.getPointer(),
                        finalX, finalY,
                        finalWidth = width != -1 ? width : videoMode.getWidth(),
                        finalHeight = height != -1 ? height : videoMode.getHeight(),
                        frameRate != -1 ? frameRate : videoMode.getRefreshRate());
                break;
            case WINDOWED_FULLSCREEN:
                setFloating(true);
                setDecorated(false);
                glfwSetWindowMonitor(pointer, NULL,
                        finalX, finalY,
                        finalWidth = videoMode.getWidth(),
                        finalHeight = videoMode.getHeight(),
                        videoMode.getRefreshRate());
                break;
            case WINDOWED:
                setFloating(lastFloating);
                setDecorated(lastDecorated);
                glfwSetWindowMonitor(pointer, NULL,
                        finalX = lastPosX, finalY = lastPosY,
                        finalWidth = width != -1 ? width : lastWidth,
                        finalHeight = height != -1 ? height : lastHeight,
                        videoMode.getRefreshRate());
                break;
        }
        this.displayMode = displayMode;
        relocate(finalX, finalY);
        resize(finalWidth, finalHeight);
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
            setFocused(true);
            GLFWContext.onShowWindow(this);
        } else {
            glfwHideWindow(pointer);
            GLFWContext.onHideWindow(this);
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
    public void prepareDraw() {
        GLFWContext.makeContextCurrent(pointer);
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(pointer);

        if (resized) resized = false;
    }

    @Override
    public void dispose() {
        if (pointer == NULL) return;

        hide();
        disposable.dispose();
        pointer = NULL;
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

    private void setFocused(boolean focused) {
        this.focused = focused;
        windowFocusCallbacks.forEach(callback -> callback.invoke(this, focused));
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

    // ================= Window Cursor Start =================
    @Override
    public CursorState getCursorState() {
        return cursorState;
    }

    @Override
    public void setCursorState(CursorState state) {
        this.cursorState = notNull(state);
        glfwSetInputMode(pointer, GLFW_CURSOR, GLFW_CURSOR_STATES[state.ordinal()]);
    }

    @Override
    public CursorShape getCursorShape() {
        return cursorShape;
    }

    @Override
    public void setCursorShape(CursorShape shape) {
        cursorShape = notNull(shape);
        if (cursorPointer != NULL) {
            glfwDestroyCursor(cursorPointer);
        }
        if (shape == CursorShape.NORMAL) {
            cursorPointer = NULL;
        } else {
            cursorPointer = glfwCreateStandardCursor(GLFW_CURSOR_SHAPES[shape.ordinal()]);
        }
        glfwSetCursor(pointer, cursorPointer);
    }
    // ================= Window Cursor End =================

    // ================= Window Callbacks Start =================
    @Override
    public void addKeyCallback(KeyCallback callback) {
        keyCallbacks.add(notNull(callback));
    }

    @Override
    public void removeKeyCallback(KeyCallback callback) {
        keyCallbacks.remove(callback);
    }

    @Override
    public void addMouseCallback(MouseCallback callback) {
        mouseCallbacks.add(notNull(callback));
    }

    @Override
    public void removeMouseCallback(MouseCallback callback) {
        mouseCallbacks.remove(callback);
    }

    @Override
    public void addCursorCallback(CursorCallback callback) {
        cursorCallbacks.add(notNull(callback));
    }

    @Override
    public void removeCursorCallback(CursorCallback callback) {
        cursorCallbacks.remove(callback);
    }

    @Override
    public void addScrollCallback(ScrollCallback callback) {
        scrollCallbacks.add(notNull(callback));
    }

    @Override
    public void removeScrollCallback(ScrollCallback callback) {
        scrollCallbacks.remove(callback);
    }

    @Override
    public void addCharModsCallback(CharModsCallback callback) {
        charModsCallbacks.add(notNull(callback));
    }

    @Override
    public void removeCharModsCallback(CharModsCallback callback) {
        charModsCallbacks.remove(callback);
    }

    @Override
    public void addWindowCloseCallback(WindowCloseCallback callback) {
        windowCloseCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowCloseCallback(WindowCloseCallback callback) {
        windowCloseCallbacks.remove(callback);
    }

    @Override
    public void addWindowFocusCallback(WindowFocusCallback callback) {
        windowFocusCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowFocusCallback(WindowFocusCallback callback) {
        windowFocusCallbacks.remove(callback);
    }

    @Override
    public void addWindowIconifyCallback(WindowIconifyCallback callback) {
        windowIconifyCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowIconifyCallback(WindowIconifyCallback callback) {
        windowIconifyCallbacks.remove(callback);
    }

    @Override
    public void addWindowMaximizeCallback(WindowMaximizeCallback callback) {
        windowMaximizeCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowMaximizeCallback(WindowMaximizeCallback callback) {
        windowMaximizeCallbacks.remove(callback);
    }

    @Override
    public void addCursorEnterCallback(CursorEnterCallback callback) {
        cursorEnterCallbacks.add(notNull(callback));
    }

    @Override
    public void removeCursorEnterCallback(CursorEnterCallback callback) {
        cursorEnterCallbacks.remove(callback);
    }

    @Override
    public void addWindowSizeCallback(WindowSizeCallback callback) {
        windowSizeCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowSizeCallback(WindowSizeCallback callback) {
        windowSizeCallbacks.remove(callback);
    }

    @Override
    public void addWindowPosCallback(WindowPosCallback callback) {
        windowPosCallbacks.add(notNull(callback));
    }

    @Override
    public void removeWindowPosCallback(WindowPosCallback callback) {
        windowPosCallbacks.remove(callback);
    }

    @Override
    public void addDropCallback(DropCallback callback) {
        dropCallbacks.add(notNull(callback));
    }

    @Override
    public void removeDropCallback(DropCallback callback) {
        dropCallbacks.remove(callback);
    }

    protected void initCallbacks() {
        glfwSetKeyCallback(pointer, (window, key, scancode, action, mods) -> {
            KeyCode _key = KeyCode.valueOf(key);
            Action _action = Action.valueOf(action);
            Modifiers modifiers = Modifiers.of(mods);
            keyCallbacks.forEach(callback -> callback.invoke(this, _key, scancode, _action, modifiers));
        });
        glfwSetMouseButtonCallback(pointer, (window, button, action, mods) -> {
            MouseButton _button = MouseButton.valueOf(button);
            Action _action = Action.valueOf(action);
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
            windowCloseCallbacks.forEach(callback -> callback.invoke(this));
            if (doCloseImmediately) {
                dispose();
            }
        });
        glfwSetWindowFocusCallback(pointer, (window, focused) -> setFocused(focused));
        glfwSetCursorEnterCallback(pointer, (window, entered) ->
                cursorEnterCallbacks.forEach(callback -> callback.invoke(this, entered)));
        glfwSetWindowPosCallback(pointer, (window, xpos, ypos) -> relocate(xpos, ypos));
        glfwSetWindowSizeCallback(pointer, (window, width, height) -> resize(width, height));
        glfwSetWindowContentScaleCallback(pointer, ((window, xscale, yscale) -> GLFWContext.refreshScale(screen)));
        glfwSetDropCallback(pointer, (window, count, names) -> {
            Path[] paths = new Path[count];
            PointerBuffer buffer = MemoryUtil.memPointerBuffer(names, count);
            for (int i = 0; i < count; i++) {
                paths[i] = Path.of(MemoryUtil.memUTF8(buffer.get(i)));
            }
            List<Path> pathList = List.of(paths);
            dropCallbacks.forEach(callback -> callback.invoke(this, pathList));
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
//        glfwSetFramebufferSizeCallback()
//        glfwSetWindowContentScaleCallback()
    }
    // ================= Window Callbacks End =================
}

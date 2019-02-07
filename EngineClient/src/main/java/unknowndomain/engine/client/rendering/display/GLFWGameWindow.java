package unknowndomain.engine.client.rendering.display;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import unknowndomain.engine.Platform;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWGameWindow implements GameWindow {

    private long handle;
    private String title;

    private int width;
    private int height;
    private boolean resized = false;
    private Matrix4f projection;

    private boolean paused;

    private boolean closed = false;

    private final List<KeyCallback> keyCallbacks = new LinkedList<>();
    private final List<MouseCallback> mouseCallbacks = new LinkedList<>();
    private final List<CursorCallback> cursorCallbacks = new LinkedList<>();
    private final List<ScrollCallback> scrollCallbacks = new LinkedList<>();
    private final List<CharCallback> charCallbacks = new LinkedList<>();

    public GLFWGameWindow(int width, int height, String title) {
        this.title = title;
        this.width = width;
        this.height = height;
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
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.resized = true;
        glViewport(0, 0, width, height);
    }

    @Override
    public Matrix4f projection() {
        if (resized || projection == null) {
            projection = new Matrix4f().perspective((float) (Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f)))), width / (float) height, 0.01f, 1000f);
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
        glfwSetWindowTitle(handle, title);
    }

    @Override
    public void showCursor() {
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    @Override
    public void disableCursor() {
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    @Override
    public boolean isCursorHidden() {
        return glfwGetInputMode(handle, GLFW_CURSOR) != GLFW_CURSOR_NORMAL;
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
    public void beginRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void endRender() {
        glfwSwapBuffers(handle);

        if (isResized()) {
            resized = false;
        }

        glfwPollEvents();
    }

    @Override
    public void close() {
        closed = true;
        glfwDestroyWindow(handle);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public long getHandle() {
        return handle;
    }

    public void init() {
        initErrorCallback(System.err);
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        initWindowHint();
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (!checkCreated())
            throw new RuntimeException("Failed to parse the GLFW window");
        initCallbacks();
        setWindowPosCenter();
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        enableVSync();
        setupInput();
        showWindow();
    }

    private void setupInput() {
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> keyCallbacks.forEach(keyCallback -> keyCallback.invoke(key, scancode, action, mods)));
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> mouseCallbacks.forEach(mouseCallback -> mouseCallback.invoke(button, action, mods)));
        glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> cursorCallbacks.forEach(cursorCallback -> cursorCallback.invoke(xpos, ypos)));
        glfwSetScrollCallback(handle, (window, xoffset, yoffset) -> scrollCallbacks.forEach(scrollCallback -> scrollCallback.invoke(xoffset, yoffset)));
        glfwSetCharCallback(handle, (window, codepoint) -> charCallbacks.forEach(charCallback -> charCallback.invoke((char) codepoint)));

        // TODO: Remove it.
        addKeyCallback((key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                if (paused) {
                    disableCursor();
                    paused = false;
                } else {
                    showCursor();
                    paused = true;
                }
            }

            if (key == GLFW_KEY_F12 && action == GLFW_PRESS) {
                Platform.getEngine().getCurrentGame().terminate();
                System.exit(0); // TODO: Remove it
            }
        });
    }

    private boolean checkCreated() {
        return handle != NULL;
    }

    private void initCallbacks() {
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> setSize(width, height));
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
    }

    private void initErrorCallback(PrintStream stream) {
        GLFWErrorCallback.createPrint(stream).set();
    }

    private void setWindowPosCenter() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(handle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
    }

    private void enableVSync() {
        glfwSwapInterval(1);
    }

    private void showWindow() {
        glfwShowWindow(handle);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // glViewport(0, 0, width, height);
        disableCursor();
    }
}

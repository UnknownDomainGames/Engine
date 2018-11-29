package unknowndomain.engine.client.rendering.display;

import org.joml.Matrix4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import unknowndomain.engine.client.EngineClient;

import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DefaultGameWindow implements GameWindow {

    private long handle;
    private String title;

    private int width;
    private int height;
    private boolean resized = false;
    private Matrix4f projection;

    private EngineClient engineClient;
    private boolean paused;

    public DefaultGameWindow(EngineClient game, int width, int height, String title) {
        this.engineClient = game;
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

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    } // TODO: Remove it.

    public void beginDraw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void endDraw() {
        glfwSwapBuffers(handle);

        if (isResized())
            resized = false;

        glfwPollEvents();
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
        setupMouseCallback();
        setupCursorCallback();
        setupKeyCallback();
        setupScrollCallback();
    }

    private boolean checkCreated() {
        return handle != NULL;
    }

    private void initCallbacks() {
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            setSize(width, height);
        });
//        glfwSetCharModsCallback(handle, (window, codepoint, mods) -> engineClient.handleTextInput(codepoint, mods));
    }

    private void initWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
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


    private void setupKeyCallback() {
        new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
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
                    System.exit(0); //FIXME: we need a way for exit game.
                }

                engineClient.getCurrentGame().getKeyBindingManager().handleKeyPress(key, scancode, action, mods);
            }
        }.set(handle);
    }

    private void setupMouseCallback() {
        new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                engineClient.getCurrentGame().getKeyBindingManager().handleMousePress(button, action, mods);
            }
        }.set(handle);
    }

    private void setupCursorCallback() {
        new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                engineClient.getCurrentGame().getController().handleCursorMove(xpos, ypos);
            }
        }.set(handle);
    }


    private void setupScrollCallback() {
        new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                engineClient.getCurrentGame().getController().handleScroll(xoffset, yoffset);
            }
        }.set(handle);
    }
}

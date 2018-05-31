package com.github.unknownstudio.unknowndomain.engine.client.display;

import com.github.unknownstudio.unknowndomain.engine.client.GameClient;
import com.github.unknownstudio.unknowndomain.engineapi.client.display.Window;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowDisplay implements Window{
    private long handle;
    private int width;
    private int height;
    private String title;
    private boolean resized;
    private GLFWMouseButtonCallback mouseBtnCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWScrollCallback scrollCallback;

    private GameClient game;

    public WindowDisplay(GameClient game, int width, int height, String title) {
        this.game = game;
        this.title = title;
        this.width = width;
        this.height = height;
        resized = false;
    }

    public void init() {
        initErrorCallback(System.err);
        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        initWindowHint();
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(!checkCreated()) throw new RuntimeException("Failed to create the GLFW window");
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
            this.width = width;
            this.height = height;
            this.resized = true;
            glViewport(0,0,width,height);
        });
        glfwSetCharModsCallback(handle, (window, codepoint, mods) -> game.handleTextInput(codepoint, mods));
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
        glfwSetWindowPos(
                handle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );
    }

    private void enableVSync() {
        glfwSwapInterval(1);
    }

    private void showWindow(){
        glfwShowWindow(handle);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glViewport(0,0,width,height);
        hideCursor();
    }

    private void setupMouseCallback() {
        mouseBtnCallback = new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods) {
				game.handleMousePress(button, action, mods);
            }
        }.set(handle);
    }

    private void setupCursorCallback() {
        cursorPosCallback = new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos) {
				game.handleCursorMove(xpos, ypos);
            }
        }.set(handle);
    }

    private void setupKeyCallback() {
        keyCallback = new GLFWKeyCallback(){
            @Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				game.handleKeyPress(key, scancode, action, mods);
			}
        }.set(handle);
    }
    
    private void setupScrollCallback() {
    	scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				game.handleScroll(xoffset, yoffset);
			}
		}.set(handle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public boolean getKey(int keyCode, int action) {
        return glfwGetKey(handle, keyCode) == action;
    }

    public void update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //Render

        game.getRenderer().render();

        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getHandle() {
        return handle;
    }

    public boolean isResized() {
        return resized;
    }

    public void showCursor(){
        glfwSetInputMode(handle,GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void hideCursor(){
        glfwSetInputMode(handle,GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
}

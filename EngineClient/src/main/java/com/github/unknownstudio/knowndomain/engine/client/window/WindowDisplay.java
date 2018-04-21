package com.github.unknownstudio.knowndomain.engine.client.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.PrintStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowDisplay {
    private long handle;
    private int width;
    private int height;
    private String title;
    private boolean resized;

    public WindowDisplay(int width, int height, String title) {
        this.title = title;
        this.width = width;
        this.height = height;
        resized = false;
    }

    public void init() {
        setupErrCallback(System.err);
        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        setupWindowHint();
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(!checkCreated()) throw new RuntimeException("Failed to create the GLFW window");
        setupResizeCallback();
        setWindowPosCenter();
        glfwMakeContextCurrent(handle);
        enableVSync();
        showWindow();

    }

    private boolean checkCreated() {
        return handle != NULL;
    }

    private void setupResizeCallback() {
        glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
            //glViewport(0,0,width,height);
        });
    }

    private void setupWindowHint() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    }

    private void setupErrCallback(PrintStream stream) {
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
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public boolean getKey(int keyCode, int action) {
        return glfwGetKey(handle, keyCode) == action;
    }

    public void update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);



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
}

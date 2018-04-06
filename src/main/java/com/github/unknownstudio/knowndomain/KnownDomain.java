package com.github.unknownstudio.knowndomain;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class KnownDomain {
    public static final String NAME = "KnownDomain";

    private long window;

    public void run() {
        init();
        loop();
        end();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        window = glfwCreateWindow(300, 300, KnownDomain.NAME, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
    }

    private void loop() {
        GL.createCapabilities();
        while ( !glfwWindowShouldClose(window) ) {
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void end() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new KnownDomain().run();
    }

}

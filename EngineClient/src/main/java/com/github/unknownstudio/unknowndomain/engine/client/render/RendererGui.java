package com.github.unknownstudio.unknowndomain.engine.client.render;

import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgramGui;
import com.github.unknownstudio.unknowndomain.engineapi.client.render.Renderer;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class RendererGui implements Renderer {

    private ShaderProgram shader;

    public RendererGui(){
        shader = new ShaderProgramGui();

    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}

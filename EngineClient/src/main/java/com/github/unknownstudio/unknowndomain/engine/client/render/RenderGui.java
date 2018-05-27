package com.github.unknownstudio.unknowndomain.engine.client.render;

import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgramGui;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class RenderGui extends Render {

    private ShaderProgram shader;

    public RenderGui(){
        shader = new ShaderProgramGui();

    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}

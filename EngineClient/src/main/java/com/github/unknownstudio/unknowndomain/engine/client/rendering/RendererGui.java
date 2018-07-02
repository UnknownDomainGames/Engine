package com.github.unknownstudio.unknowndomain.engine.client.rendering;

import com.github.unknownstudio.unknowndomain.engine.client.gui.Gui;
import com.github.unknownstudio.unknowndomain.engineapi.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgramGui;
import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.Renderer;

import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.RenderingLayer;
import org.joml.Matrix4f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class RendererGui implements RenderingLayer {

    private ShaderProgram shader;
    private AWTFontRenderer fontRenderer;

    public RendererGui(){
        shader = new ShaderProgramGui();
        shader.createShader();
        fontRenderer = new AWTFontRenderer(new Font("Microsoft JhengHei UI", Font.PLAIN, 16), "ascii");
    }

    @Override
    public void render() {
        shader.useShader();
        shader.setUniform("projection", new Matrix4f().identity().ortho(0,854f,480f,0,-1000f,2000f));
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        fontRenderer.drawText("The quick brown fox jumps over the lazy dog.", 0,0,0xffffffff);
        RenderingLayer.super.render(shader);
    }

    @Override
    public void putRenderer(Renderer renderer) {
        if(renderer instanceof Gui){
            RenderingLayer.super.putRenderer(renderer);
        }
    }
}

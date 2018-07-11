package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.client.rendering.RenderingLayer;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.client.gui.Gui;
import unknowndomain.engine.client.shader.ShaderProgramGui;

import org.joml.Matrix4f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * render for any gui
 */
public class RendererGui extends RenderingLayer {

    private ShaderProgramGui shader;
    private AWTFontRenderer fontRenderer;
    private TTFFontRenderer fontRenderer1;

    public RendererGui(){
        shader = new ShaderProgramGui();
        shader.createShader();
        //fontRenderer = new AWTFontRenderer(new Font("Microsoft JhengHei UI", Font.PLAIN, 16), "ascii");
        fontRenderer1 = new TTFFontRenderer("assets/unknowndomain/fonts/arial.ttf");
    }

    @Override
    public void render() {
        shader.useShader();
        shader.setUniform("projection", new Matrix4f().identity().ortho(0,854f,480f,0,-1000f,2000f));
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        shader.setUseAlphaChannel(true);
        fontRenderer1.drawText("The quick brown fox jumps over the lazy dog.", 0,0,0xffffffff, 16);
        super.render();
    }

    @Override
    public void putRenderer(Renderer renderer) {
        if(renderer instanceof Gui){
            super.putRenderer(renderer);
        }
    }
}

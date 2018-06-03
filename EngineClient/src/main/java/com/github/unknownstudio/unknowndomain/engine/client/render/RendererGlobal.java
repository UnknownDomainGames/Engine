package com.github.unknownstudio.unknowndomain.engine.client.render;

import com.github.unknownstudio.unknowndomain.engine.client.display.CameraDefault;
import com.github.unknownstudio.unknowndomain.engine.client.resource.FileTexture2D;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgramDefault;
import com.github.unknownstudio.unknowndomain.engine.client.util.BufferBuilder;
import com.github.unknownstudio.unknowndomain.engineapi.client.display.Camera;
import com.github.unknownstudio.unknowndomain.engineapi.client.render.Renderer;
import org.lwjgl.opengl.GL11;

import java.net.URISyntaxException;

/**
 * render for the scene
 */
public final class RendererGlobal implements Renderer {

    private ShaderProgram shader;
    private Camera camera;
    private RendererGui gui; //FIXME: maybe for test purpose only

    public RendererGlobal() {
        shader = new ShaderProgramDefault();
        shader.createShader();
        camera = new CameraDefault();
        camera.moveTo(0,0,-5);
        camera.rotateTo(90,0);
        gui = new RendererGui();
    }

    private FileTexture2D tmp;

    {
        try {
            tmp = new FileTexture2D(RendererGlobal.class.getResource("/assets/tmp/tmp.png").toURI());
            if (!tmp.loadImage())
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); //Stub
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        shader.useShader();
        shader.setUniform("projection", camera.makeProjectionMatrix(854,480));
        shader.setUniform("view", camera.makeViewMatrix());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tmp.useTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, true, true, true);

        //FRONT
        bufferBuilder.pos(-0.5f, -0.5f, 0.5f).color(1, 0, 0f, 1.0f).tex(0, 1).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, 0.5f).color(0, 1, 0f, 1.0f).tex(1, 1).endVertex();
        bufferBuilder.pos(0.5f, 0.5f, 0.5f).color(0, 0, 1f, 1.0f).tex(1, 0).endVertex();
        bufferBuilder.pos(-.5f, 0.5f, 0.5f).color(1, 1, 1, 1.0f).tex(0, 0).endVertex();

        //-VE X
        bufferBuilder.pos(-0.5f, 0.5f, 0.5f).color(1, 1, 1f, 1.0f).tex(1,0).endVertex();
        bufferBuilder.pos(-.5f, 0.5f, -0.5f).color(0, 1, 0, 1.0f).tex(0,0).endVertex();
        bufferBuilder.pos(-0.5f, -0.5f, -0.5f).color(0, 0, 1f, 1.0f).tex(0,1).endVertex();
        bufferBuilder.pos(-0.5f, -0.5f, 0.5f).color(1, 0, 0f, 1.0f).tex(1,1).endVertex();
        //-VE Y
        bufferBuilder.pos(-0.5f, -0.5f, 0.5f).color(1, 0, 0f, 1.0f).tex(0,0).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, 0.5f).color(0, 1, 0f, 1.0f).tex(1,0).endVertex();
        bufferBuilder.pos(.5f, -0.5f, -0.5f).color(1, 1, 1, 1.0f).tex(1,1).endVertex();
        bufferBuilder.pos(-0.5f, -0.5f, -0.5f).color(0, 0, 1f, 1.0f).tex(0,1).endVertex();
        //BACK
        bufferBuilder.pos(-0.5f, -0.5f, -0.5f).color(0, 0, 1f, 1.0f).tex(0, 1).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, -0.5f).color(1, 1, 1f, 1.0f).tex(1, 1).endVertex();
        bufferBuilder.pos(0.5f, 0.5f, -0.5f).color(1, 0, 0f, 1.0f).tex(1, 0).endVertex();
        bufferBuilder.pos(-.5f, 0.5f, -0.5f).color(0, 1, 0, 1.0f).tex(0, 0).endVertex();
        //+VE Y
        bufferBuilder.pos(-.5f, 0.5f, -0.5f).color(0, 1, 0, 1.0f).tex(0,0).endVertex();
        bufferBuilder.pos(-0.5f, 0.5f, 0.5f).color(1, 1, 1f, 1.0f).tex(0,1).endVertex();
        bufferBuilder.pos(0.5f, 0.5f, 0.5f).color(0, 0, 1f, 1.0f).tex(1,1).endVertex();
        bufferBuilder.pos(0.5f, 0.5f, -0.5f).color(1, 0, 0f, 1.0f).tex(1,0).endVertex();
        //+VE X
        bufferBuilder.pos(0.5f, 0.5f, -0.5f).color(1, 0, 0f, 1.0f).tex(1,0).endVertex();
        bufferBuilder.pos(.5f, 0.5f, 0.5f).color(0, 0, 1, 1.0f).tex(0,0).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, 0.5f).color(0, 1, 0f, 1.0f).tex(0,1).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, -0.5f).color(1, 1, 1f, 1.0f).tex(1,1).endVertex();

        tessellator.draw();
        gui.render();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void onCursorMoved(double x, double y){
        camera.rotate((float)x,(float)y);
    }

    public void destroy() {
        shader.deleteShader();
    }

    public Camera getCamera() {
        return camera;
    }
}

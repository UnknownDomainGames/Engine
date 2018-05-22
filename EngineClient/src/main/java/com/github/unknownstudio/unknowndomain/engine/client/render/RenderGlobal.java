package com.github.unknownstudio.unknowndomain.engine.client.render;

import com.github.unknownstudio.unknowndomain.engine.client.resource.Texture2D;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgramDefault;
import com.github.unknownstudio.unknowndomain.engine.client.util.BufferBuilder;
import com.github.unknownstudio.unknowndomain.engine.client.util.VertexBufferObject;
import org.lwjgl.opengl.GL11;

import java.net.URISyntaxException;

public final class RenderGlobal extends Render {

    private ShaderProgram shader;
    private VertexBufferObject vbo;
    private BufferBuilder bufferBuilder;

    public RenderGlobal() {
        shader = new ShaderProgramDefault();
        shader.createShader();
        vbo = new VertexBufferObject();
        bufferBuilder = new BufferBuilder(1048576);
    }

    private Texture2D tmp;

    {
        try {
            tmp = new Texture2D(RenderGlobal.class.getResource("/assets/tmp/tmp.png").toURI());
            if (!tmp.loadImage())
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); //Stub
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        shader.useShader();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tmp.useTexture();
        bufferBuilder.begin(GL11.GL_QUADS, true, true, true);

        //FRONT
        bufferBuilder.pos(-0.5f, -0.5f, 0.5f).color(1, 0, 0f, 1.0f).tex(0, 1).endVertex();
        bufferBuilder.pos(0.5f, -0.5f, 0.5f).color(0, 1, 0f, 1.0f).tex(1, 1).endVertex();
        bufferBuilder.pos(0.5f, 0.5f, 0.5f).color(0, 0, 1f, 1.0f).tex(1, 0).endVertex();
        bufferBuilder.pos(-.5f, 0.5f, 0.5f).color(1, 1, 1, 1.0f).tex(0, 0).endVertex();

        //-VE X
        bufferBuilder.pos(-0.5f, 0.5f, 0.5f).color(1, 1, 1f, 1.0f).tex(1,0).endVertex();
        bufferBuilder.pos(-.5f, 0.5f, -0.5f).color(0, 0, 1, 1.0f).tex(0,0).endVertex();
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

        bufferBuilder.finish();
        draw(bufferBuilder);
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void draw(BufferBuilder buffer) {
        if (buffer.isDrawing()) buffer.finish();
        shader.useShader();
        vbo.bind();
        vbo.uploadData(buffer);
        vbo.bind();

        int posarr;
        if (buffer.isPosEnabled()) {
            posarr = shader.getAttributeLocation("position");
            shader.pointVertexAttribute(posarr, 3, buffer.getOffset(), 0);
            shader.enableVertexAttrib(posarr);
        } else {
        }
        if (buffer.isTexEnabled()) {
            posarr = shader.getAttributeLocation("texcoord");
            shader.pointVertexAttribute(posarr, 2, buffer.getOffset(), (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
            shader.enableVertexAttrib(posarr);
        }
        if (buffer.isColorEnabled()) {
            posarr = shader.getAttributeLocation("color");
            shader.pointVertexAttribute(posarr, 4, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0)) * Float.BYTES);
            shader.enableVertexAttrib(posarr);
        }
        vbo.unbind();
        vbo.bindVAO();

        vbo.drawArrays(buffer.getDrawMode());
        vbo.unbind();
        buffer.reset();

    }

    public void destroy() {
        vbo.delete();
        shader.deleteShader();
    }
}

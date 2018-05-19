package com.github.unknownstudio.unknowndomain.engine.client.render;

import com.github.unknownstudio.unknowndomain.engine.client.resource.Texture2D;
import com.github.unknownstudio.unknowndomain.engine.client.shader.Shader;
import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderDefault;
import com.github.unknownstudio.unknowndomain.engine.client.util.BufferBuilder;
import com.github.unknownstudio.unknowndomain.engine.client.util.VertexBufferObject;
import org.lwjgl.opengl.GL11;

import java.net.URISyntaxException;

public final class RenderGlobal extends Render {

    private Shader shader;
    private VertexBufferObject vbo;
    private BufferBuilder bufferBuilder;

    public RenderGlobal() {
        shader = new ShaderDefault();
        shader.createShader();
        vbo = new VertexBufferObject();
        bufferBuilder = new BufferBuilder(1048576);
    }
    private Texture2D tmp;

    {
        try {
            tmp = new Texture2D(RenderGlobal.class.getResource("/assets/tmp/tmp.png").toURI());
            tmp.loadImage();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        shader.useShader();
        /*glBegin (GL_TRIANGLES);
        glColor3f (1, 0, 0.7f);
        glVertex3f (6, 4, 0); // Vertex one
        glColor3f (1, 0, 0.7f);
        glVertex3f (4, 8, 0); // Vertex two
        glColor3f (1, 0, 0.7f);
        glVertex3f (8, 8, 0); // Vertex three
        glEnd();*/
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        tmp.useTexture();
        bufferBuilder.begin(GL11.GL_QUADS, true, true, true);
        bufferBuilder.pos(-1, -1, 0).color(1, 0, 0.7f, 1.0f).tex(0,1).endVertex();
        bufferBuilder.pos(0, -1, 0).color(1, 0, 0.7f, 1.0f).tex(1,1).endVertex();
        bufferBuilder.pos(0, 0, 0).color(1, 0, 0.7f, 1.0f).tex(1,0).endVertex();
        bufferBuilder.pos(-1, 0, 0).color(1, 0, 0.7f, 1.0f).tex(0,0).endVertex();
        bufferBuilder.finish();
        draw(bufferBuilder);
    }

    public Shader getShader() {
        return shader;
    }

    public void draw(BufferBuilder buffer) {
        if (!buffer.isDrawing()) {
            shader.useShader();
            vbo.bind();
            vbo.uploadData(buffer);

            int posarr;
            if (buffer.isPosEnabled()) {
                posarr = shader.getAttributeLocation("position");
                shader.enableVertexAttrib(posarr);
                shader.pointVertexAttribute(posarr, 3, buffer.getOffset() * Float.BYTES, 0);
            } else {
            }
            if (buffer.isTexEnabled()) {
                posarr = shader.getAttributeLocation("texcoord");
                shader.enableVertexAttrib(posarr);
                shader.pointVertexAttribute(posarr, 2, buffer.getOffset() * Float.BYTES, (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
            }
            if (buffer.isColorEnabled()) {
                posarr = shader.getAttributeLocation("color");
                shader.enableVertexAttrib(posarr);
                shader.pointVertexAttribute(posarr, 4, buffer.getOffset() * Float.BYTES, ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0)) * Float.BYTES);
            }

            vbo.drawArrays(buffer.getDrawMode());
        }
    }

    public void destroy() {
        vbo.delete();
        shader.deleteShader();
    }
}

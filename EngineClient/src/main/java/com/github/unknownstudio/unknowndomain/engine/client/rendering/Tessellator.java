package com.github.unknownstudio.unknowndomain.engine.client.rendering;

import com.github.unknownstudio.unknowndomain.engine.client.shader.ShaderProgram;
import com.github.unknownstudio.unknowndomain.engine.client.util.BufferBuilder;
import com.github.unknownstudio.unknowndomain.engine.client.util.VertexBufferObject;

public class Tessellator {

    private BufferBuilder buffer;
    private VertexBufferObject vbo;
    
    private static final Tessellator INSTANCE = new Tessellator(1048576);

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public Tessellator(int bufferSize){
        vbo = new VertexBufferObject();
        buffer = new BufferBuilder(bufferSize);
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }
    
    public void draw(){
        buffer.finish();
        vbo.bind();
        vbo.uploadData(buffer);
        vbo.bind();

        int posarr;
        if (buffer.isPosEnabled()) {
            ShaderProgram.pointVertexAttribute(0, 3, buffer.getOffset(), 0);
            ShaderProgram.enableVertexAttrib(0);
        }
        if (buffer.isTexEnabled()) {
            ShaderProgram.pointVertexAttribute(2, 2, buffer.getOffset(), (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
            ShaderProgram.enableVertexAttrib(2);
        }
        if (buffer.isColorEnabled()) {
            ShaderProgram.pointVertexAttribute(1, 4, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0)) * Float.BYTES);
            ShaderProgram.enableVertexAttrib(1);
        }
        vbo.unbind();
        vbo.bindVAO();

        vbo.drawArrays(buffer.getDrawMode());
        vbo.unbind();
        buffer.reset();
    }
}

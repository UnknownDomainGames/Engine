package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.client.util.BufferBuilder;
import unknowndomain.engine.client.util.VertexBufferObject;

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
            Shader.pointVertexAttribute(0, 3, buffer.getOffset(), 0);
            Shader.enableVertexAttrib(0);
        }
        if (buffer.isTexEnabled()) {
            Shader.pointVertexAttribute(2, 2, buffer.getOffset(), (buffer.isPosEnabled() ? 3 : 0) * Float.BYTES);
            Shader.enableVertexAttrib(2);
        }
        if (buffer.isColorEnabled()) {
            Shader.pointVertexAttribute(1, 4, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0)) * Float.BYTES);
            Shader.enableVertexAttrib(1);
        }
        if (buffer.isNormalEnabled()) {
            Shader.pointVertexAttribute(3, 3, buffer.getOffset(), ((buffer.isPosEnabled() ? 3 : 0) + (buffer.isTexEnabled() ? 2 : 0) + (buffer.isColorEnabled() ? 4 : 0)) * Float.BYTES);
            Shader.enableVertexAttrib(3);
        }
        vbo.unbind();
        vbo.bindVAO();

        vbo.drawArrays(buffer.getDrawMode());
        vbo.unbind();
        buffer.reset();
    }
}

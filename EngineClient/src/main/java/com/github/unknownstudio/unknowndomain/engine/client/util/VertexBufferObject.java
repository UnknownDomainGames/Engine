package com.github.unknownstudio.unknowndomain.engine.client.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

public class VertexBufferObject {
    private int id = -1;
    private int count;

    public VertexBufferObject(){
        id = GL15.glGenBuffers();
    }

    public void bind(){
        bind(GL15.GL_ARRAY_BUFFER);
    }

    public void bind(int target){
        GL15.glBindBuffer(target, id);
    }

    public void unbind(){
        unbind(GL15.GL_ARRAY_BUFFER);
    }

    public void unbind(int target){
        GL15.glBindBuffer(target, 0);
    }

    public void uploadData(BufferBuilder builder){
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder.build(), GL15.GL_STATIC_DRAW);
        unbind();
        this.count = builder.getVertexCount();
    }

    public void uploadData(ByteBuffer builder){
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, builder, GL15.GL_STATIC_DRAW);
        unbind();
        this.count = builder.limit(); //FIXME: should be vertex count but not bytes count
    }

    public void drawArrays(int mode){
        GL11.glDrawArrays(mode,0,this.count);
    }

    public void delete(){
        if (id != -1){
            GL15.glDeleteBuffers(id);
            id = -1;
        }
    }
}

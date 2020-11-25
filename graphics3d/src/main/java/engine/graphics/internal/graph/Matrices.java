package engine.graphics.internal.graph;

import engine.graphics.util.Struct;
import org.joml.Matrix4fc;

import java.nio.ByteBuffer;

public class Matrices implements Struct {
    private Matrix4fc projMatrix;
    private Matrix4fc viewMatrix;
    private Matrix4fc modelMatrix;

    public Matrices(Matrix4fc projMatrix, Matrix4fc viewMatrix, Matrix4fc modelMatrix) {
        this.projMatrix = projMatrix;
        this.viewMatrix = viewMatrix;
        this.modelMatrix = modelMatrix;
    }

    @Override
    public int sizeof() {
        return 192;
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        projMatrix.get(index, buffer);
        viewMatrix.get(index + 64, buffer);
        modelMatrix.get(index + 128, buffer);
        return buffer;
    }
}

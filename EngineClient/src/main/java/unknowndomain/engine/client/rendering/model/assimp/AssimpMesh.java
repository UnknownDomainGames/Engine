package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class AssimpMesh {
    private final AIMesh mesh;
    private final int vertexBuf;
    private final int texBuf;
    private final int normalBuf;
    private final int elementCount;
    private final int elementArrayBuffer;

    public AssimpMesh(AIMesh mesh){
        this.mesh = mesh;

        vertexBuf = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuf);
        var aivertex = mesh.mVertices();
        GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, AIVector3D.SIZEOF * aivertex.remaining(), aivertex.address(), GL30.GL_STATIC_DRAW);

        texBuf = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, texBuf);
        var aitex = mesh.mTextureCoords();

        GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, aitex.sizeof() * aitex.remaining(), aitex.address(), GL30.GL_STATIC_DRAW);

        normalBuf = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, normalBuf);
        var ainormal = mesh.mVertices();
        GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, AIVector3D.SIZEOF * ainormal.remaining(), ainormal.address(), GL30.GL_STATIC_DRAW);

        int faceCount = mesh.mNumFaces();
        elementCount = faceCount * 3;
        IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        elementArrayBufferData.flip();
        elementArrayBuffer = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, elementArrayBufferData,
                GL30.GL_STATIC_DRAW);
    }

    public int getVertexBufferId() {
        return vertexBuf;
    }

    public int getTexCoordBufferId() {
        return texBuf;
    }

    public int getNormalBufferId() {
        return normalBuf;
    }

    public int getElementArrayBufferId() {
        return elementArrayBuffer;
    }

    public int getElementCount() {
        return elementCount;
    }
}

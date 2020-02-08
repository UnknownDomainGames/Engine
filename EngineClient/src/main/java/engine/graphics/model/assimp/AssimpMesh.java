package engine.graphics.model.assimp;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class AssimpMesh {

    public static final int MAX_WEIGHTS = 4;

    private final AIMesh mesh;
    private final String name;
    private String matName;

    private final int vertexBuf;
    private final int texBuf;
    private final int normalBuf;
    private final int tangentBuf;
    private final int boneBuf;
    private final int weightBuf;
    private final int elementCount;
    private final int elementArrayBuffer;

    private final List<AssimpBone> bones;

    public AssimpMesh(AIMesh mesh) {
        this.mesh = mesh;
        name = mesh.mName().dataString();
        vertexBuf = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuf);
        var aivertex = mesh.mVertices();
        GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, AIVector3D.SIZEOF * aivertex.remaining(), aivertex.address(), GL30.GL_STATIC_DRAW);

        var texcount = mesh.mTextureCoords().remaining();
        var aitex = mesh.mTextureCoords(0);
        if (aitex != null) {
            var fb = BufferUtils.createFloatBuffer(aitex.remaining() * 2);
            var count = aitex.remaining();
            for (int i = 0; i < count; i++) {
                AIVector3D textCoord = aitex.get();
                fb.put(textCoord.x());
                fb.put(textCoord.y());
            }
            fb.flip();
            texBuf = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, texBuf);

            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, fb, GL30.GL_STATIC_DRAW);
        } else {
            texBuf = 0;
        }
        var ainormal = mesh.mNormals();
        if (ainormal != null) {
            normalBuf = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, normalBuf);
            GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, AIVector3D.SIZEOF * ainormal.remaining(), ainormal.address(), GL30.GL_STATIC_DRAW);
        } else {
            normalBuf = 0;
        }
        var aitangent = mesh.mTangents();
        if (aitangent != null) {
            tangentBuf = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, tangentBuf);
            GL30.nglBufferData(GL30.GL_ARRAY_BUFFER, AIVector3D.SIZEOF * aitangent.remaining(), aitangent.address(), GL30.GL_STATIC_DRAW);
        } else {
            tangentBuf = 0;
        }


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

        bones = new ArrayList<>();
        List<Integer> boneid = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        AssimpBone.processBones(mesh, bones, boneid, weights);
        boneBuf = GL30.glGenBuffers();
        var bonesBuffer = MemoryUtil.memAllocInt(weights.size());
        bonesBuffer.put(ArrayUtils.toPrimitive(boneid.<Integer>toArray(new Integer[0]))).flip();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, boneBuf);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bonesBuffer, GL30.GL_STATIC_DRAW);

        weightBuf = GL30.glGenBuffers();
        var weightsBuffer = MemoryUtil.memAllocFloat(weights.size());
        weightsBuffer.put(ArrayUtils.toPrimitive(weights.<Float>toArray(new Float[0]))).flip();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, weightBuf);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, weightsBuffer, GL30.GL_STATIC_DRAW);

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

    public int getTangentBufferId() {
        return tangentBuf;
    }

    public int getBoneIdBufferId() {
        return boneBuf;
    }

    public int getVertexWeightBufferId() {
        return weightBuf;
    }

    public int getElementArrayBufferId() {
        return elementArrayBuffer;
    }

    public int getElementCount() {
        return elementCount;
    }

    public AIMesh getRawMesh() {
        return mesh;
    }

    public String getName() {
        return name;
    }

    public List<AssimpBone> getBones() {
        return bones;
    }

    void assignMaterialName(String matName) {
        this.matName = matName;
    }

    public String getMaterialName() {
        return matName;
    }
}

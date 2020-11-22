package engine.graphics.model.assimp;

import engine.graphics.Geometry;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.queue.RenderType;
import engine.graphics.util.DataType;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexElement;
import engine.graphics.vertex.VertexFormat;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class AssimpMesh extends Geometry {

    public static final int MAX_WEIGHTS = 4;

    public static final VertexElement BONE = new VertexElement(DataType.FLOAT, "Bones", 4);
    public static final VertexElement WEIGHT = new VertexElement(DataType.FLOAT, "Weights", 4);
    public static final RenderType ASSIMP_MODEL = RenderType.create("AssimpModel");

    private final AssimpModel parent;

    private final AIMesh mesh;
    private final String name;
    private String matName;

    private final ByteBuffer vertexBuf;
    private final FloatBuffer texBuf;
    private final ByteBuffer normalBuf;
    private final ByteBuffer tangentBuf;
    private final IntBuffer boneBuf;
    private final FloatBuffer weightBuf;
    private final int elementCount;
    private final IntBuffer elementArrayBuffer;

    public AssimpMesh(AssimpModel assimpModel, AIMesh mesh) {
        super(ASSIMP_MODEL);
        this.parent = assimpModel;
        this.mesh = mesh;
        name = mesh.mName().dataString();
        var aivertex = mesh.mVertices();
        vertexBuf = ByteBuffer.allocateDirect(AIVector3D.SIZEOF * aivertex.remaining());
        MemoryUtil.memCopy(aivertex.address(), MemoryUtil.memAddress(vertexBuf), AIVector3D.SIZEOF * aivertex.remaining());

        var texcount = mesh.mTextureCoords().remaining();
        var aitex = mesh.mTextureCoords(0);
        if (aitex != null) {
            texBuf = BufferUtils.createFloatBuffer(aitex.remaining() * 2);
            var count = aitex.remaining();
            for (int i = 0; i < count; i++) {
                AIVector3D textCoord = aitex.get();
                texBuf.put(textCoord.x());
                texBuf.put(textCoord.y());
            }
            texBuf.flip();
        } else {
            texBuf = null;
        }
        var ainormal = mesh.mNormals();
        if (ainormal != null) {
            normalBuf = ByteBuffer.allocateDirect(AIVector3D.SIZEOF * ainormal.remaining());
            MemoryUtil.memCopy(ainormal.address(), MemoryUtil.memAddress(normalBuf), AIVector3D.SIZEOF * ainormal.remaining());
        } else {
            normalBuf = null;
        }
        var aitangent = mesh.mTangents();
        if (aitangent != null) {
            tangentBuf = ByteBuffer.allocateDirect(AIVector3D.SIZEOF * aitangent.remaining());
            MemoryUtil.memCopy(aitangent.address(), MemoryUtil.memAddress(tangentBuf), AIVector3D.SIZEOF * aitangent.remaining());
        } else {
            tangentBuf = null;
        }


        int faceCount = mesh.mNumFaces();
        elementCount = faceCount * 3;
        elementArrayBuffer = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBuffer.put(face.mIndices());
        }

        elementArrayBuffer.flip();

        List<Integer> boneid = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        AssimpBone.processBones(assimpModel, mesh, boneid, weights);
        boneBuf = MemoryUtil.memAllocInt(weights.size());
        boneBuf.put(boneid.stream().mapToInt(Integer::intValue).toArray()).flip();

        weightBuf = MemoryUtil.memAllocFloat(weights.size());
        weightBuf.put(ArrayUtils.toPrimitive(weights.<Float>toArray(new Float[0]))).flip();
        var builder = MultiBufMesh.builder().drawMode(DrawMode.TRIANGLES)
                .attribute(VertexFormat.POSITION, vertexBuf).indices(elementArrayBuffer);
        if (texBuf != null) {
            builder.attribute(VertexFormat.TEX_COORD, MemoryUtil.memByteBuffer(texBuf));
        }
        if (normalBuf != null) {
            builder.attribute(VertexFormat.NORMAL, normalBuf);
        }
        if (tangentBuf != null) {
            builder.attribute(VertexFormat.TANGENT, tangentBuf);
        }
        if (weightBuf != null) {
            builder.attribute(VertexFormat.of(WEIGHT), MemoryUtil.memByteBuffer(weightBuf));
        }
        if (boneBuf != null) {
            builder.attribute(VertexFormat.of(BONE), MemoryUtil.memByteBuffer(boneBuf));
        }
        setMesh(builder.setStatic().build());

    }

    public AssimpModel getMeshParent() {
        return parent;
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

    void assignMaterialName(String matName) {
        this.matName = matName;
    }

    public String getMaterialName() {
        return matName;
    }

}

package unknowndomain.engine.client.model;

import com.google.common.collect.Lists;
import org.lwjgl.system.MemoryUtil;
import unknowndomain.engine.client.resource.Pipeline;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class MeshToGLNode implements Pipeline.Node {
    @Override
    public Object process(Pipeline.Context context, Object in) {
        if (in instanceof Mesh) {
            return convert((Mesh) in);
        } else if (in instanceof List) {
            List<Mesh> meshes = (List<Mesh>) in;
            List<GLMesh> glMeshes = new ArrayList<>();
            for (Mesh mesh : meshes) {
                glMeshes.add(convert(mesh));
            }
            return glMeshes;
        } else {
            return new ArrayList<Mesh>();
        }
    }

    public GLMesh convert(Mesh t) {
        if (t == null) return null;

        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        int[] indices = t.getIndices();
        float[] positions = t.getVertices();
        float[] normals = t.getNormals();
        float[] textCoords = t.getUv();
        int mode = t.getMode();
        try {
            int vertexCount = indices.length;
            int[] vboIdList = new int[3];

            int vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            int vboId = glGenBuffers();
            vboIdList[0] = (vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            vboId = glGenBuffers();
            vboIdList[1] = (vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // vboId = glGenBuffers();
            // vboIdList[2] = (vboId);
            // textCoordsBuffer = MemoryUtil.memAllocFloat(normals.length);
            // textCoordsBuffer.put(normals).flip();
            // glBindBuffer(GL_ARRAY_BUFFER, vboId);
            // glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            // glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            vboId = glGenBuffers();
            vboIdList[2] = (vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            return new GLMesh(vaoId, vboIdList, vertexCount, mode);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }
}
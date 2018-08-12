package unknowndomain.engine.client.model.pipeline;

import org.joml.AABBd;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.model.Mesh;

import java.util.ArrayList;
import java.util.List;

public class BoundingBoxToMesh {
    public Mesh bakeModel(AABBd model) {
        if (model == null) return null;
        float[] vertices = new float[24 * 3];
        List<Integer> indices = new ArrayList<>();
        int vertIndex = 0;
        int indx = 0;
        // north
        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        // south
        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;


        // left
        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        // right
        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;


        // bottom
        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.minY;
        vertices[vertIndex++] = (float) model.minZ;


        // top
        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        vertices[vertIndex++] = (float) model.maxX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.minZ;

        vertices[vertIndex++] = (float) model.minX;
        vertices[vertIndex++] = (float) model.maxY;
        vertices[vertIndex++] = (float) model.maxZ;

        for (int i = 0; i < 6; i++) {
            indices.add(indx);
            indices.add(indx + 1);
            indices.add(indx + 2);
            indices.add(indx);
            indices.add(indx + 2);
            indices.add(indx + 3);
            indx += 4;
        }
        int[] ind = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) ind[i] = indices.get(i);
        return new Mesh(vertices, null, null, ind, GL11.GL_LINES);
    }
}

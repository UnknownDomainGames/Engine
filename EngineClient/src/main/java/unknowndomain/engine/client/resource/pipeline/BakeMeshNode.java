package unknowndomain.engine.client.resource.pipeline;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.model.Mesh;

import java.util.ArrayList;
import java.util.List;

class BakeMeshNode implements ResourcePipeline.Node {
    Mesh bakeModel(Model model) {
        float[] vertices = new float[model.elements.length * 24 * 3];
        float[] uv = new float[model.elements.length * 24 * 2];
        float[] normals = new float[model.elements.length * 24 * 3];
        int[] indices = new int[model.elements.length * 24];
        int vertIndex = 0;
        int uvIndex = 0;
        final int X = 0, Y = 1, Z = 2;
        for (int i = 0; i < indices.length; i++) indices[i] = i;

        for (Model.Element element : model.elements) {
            float[] thisUv;

            // front
            thisUv = element.faces.north.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];


            // left
            thisUv = element.faces.west.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];

            // bottom
            thisUv = element.faces.down.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];

            // back
            thisUv = element.faces.north.uv;
            System.arraycopy(element.to, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];


            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];

            // right
            thisUv = element.faces.east.uv;
            System.arraycopy(element.to, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];

            // top
            thisUv = element.faces.up.uv;
            System.arraycopy(element.from, 0, vertices, vertIndex, 3);
            vertIndex += 3;
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[1];


            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[4];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[3];
            uv[uvIndex++] = thisUv[2];

        }
        return new Mesh(vertices, uv, normals, indices, GL11.GL_QUADS);
    }

    @Override
    public void process(ResourcePipeline.Context context) {
        List<Model> models = context.in("MappedResolvedModel");
        List<Mesh> meshes = new ArrayList<>(models.size());
        for (Model model : models) {
            meshes.add(bakeModel(model));
        }
        context.out("BakedMeshes", meshes);
    }
}

package unknowndomain.engine.client.resource.pipeline;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.api.resource.Pipeline;
import unknowndomain.engine.client.model.Mesh;

import java.util.ArrayList;
import java.util.List;

public class ModelToMeshNode implements Pipeline.Node {
    @Override
    public Object process(Pipeline.Context context, Object in) {
        List<Model> models = (List<Model>) in;
        List<Mesh> meshes = new ArrayList<>(models.size());
        for (Model model : models) {
            meshes.add(bakeModel(model));
        }
        return meshes;
    }

    Mesh bakeModel(Model model) {
        if (model == null) return null;
        float[] vertices = new float[model.elements.length * 24 * 3];
        float[] uv = new float[model.elements.length * 24 * 2];
        float[] normals = new float[model.elements.length * 24 * 3];
//        int[] indices = new int[model.elements.length * 24];
        List<Integer> indices = new ArrayList<>();
        int vertIndex = 0;
        int uvIndex = 0;
        int indx = 0;
        final int X = 0, Y = 1, Z = 2;
//        for (int i = 0; i < indices.length; i++)
//            indices[i] = i;

        for (Model.Element e : model.elements) {
            for (int i = 0; i < e.from.length; i++) {
                e.from[i] /= 16f;
            }
            for (int i = 0; i < e.to.length; i++) {
                e.to[i] /= 16f;
            }
        }

        for (Model.Element element : model.elements) {
            float[] thisUv;

            // north
            thisUv = element.faces.north.uv;
            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];

            // south
            thisUv = element.faces.south.uv;
            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];


            // left
            thisUv = element.faces.west.uv;
            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];

            // right
            thisUv = element.faces.east.uv;
            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];


            // bottom
            thisUv = element.faces.down.uv;
            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.from[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];


            // top
            thisUv = element.faces.up.uv;
            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[1];

            vertices[vertIndex++] = element.to[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[0];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.from[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[3];

            vertices[vertIndex++] = element.from[X];
            vertices[vertIndex++] = element.to[Y];
            vertices[vertIndex++] = element.to[Z];
            uv[uvIndex++] = thisUv[2];
            uv[uvIndex++] = thisUv[1];

            for (int i = 0; i < 6; i++) {
                indices.add(indx);
                indices.add(indx + 1);
                indices.add(indx + 2);
                indices.add(indx);
                indices.add(indx + 2);
                indices.add(indx + 3);
                indx += 4;
            }
        }
        int[] ind = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) ind[i] = indices.get(i);
        return new Mesh(vertices, uv, normals, ind, GL11.GL_TRIANGLES);
    }

}

package unknowndomain.engine.client.rendering.model.voxel;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import unknowndomain.engine.client.rendering.texture.TextureAtlasPart;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferPool;
import unknowndomain.engine.math.Math2;
import unknowndomain.engine.util.Facing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelBaker {

    private final VoxelModelManager modelManager;

    public ModelBaker(VoxelModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public Model bake(ModelData modelData) {
        BakedModelPrimer primer = new BakedModelPrimer();

        List<ModelData.Element> elements = modelData.parent == null ? modelData.elements : modelManager.getModel(modelData.parent).elements;
        for (ModelData.Element element : elements) {
            ModelData.Element.Cube cube = (ModelData.Element.Cube) element;

            ModelData.Element.Cube.Face[] faces = cube.faces;
            for (Facing facing : Facing.values()) {
                ModelData.Element.Cube.Face face = faces[facing.index];
                if (face == null)
                    continue;

                bakeFace(cube, face, facing, primer.getBuffer(face.cullFace));
            }

        }
        return primer.build();
    }

    private void bakeFace(ModelData.Element.Cube cube, ModelData.Element.Cube.Face face, Facing facing, GLBuffer buffer) {
        TextureAtlasPart textureAtlasPart = face.texture.textureAtlasPart;
        float u = textureAtlasPart.getMaxU() - textureAtlasPart.getMinU();
        float v = textureAtlasPart.getMaxV() - textureAtlasPart.getMinV();
        float minU = textureAtlasPart.getMinU() + u * face.texture.uv.x();
        float maxU = textureAtlasPart.getMinU() + u * face.texture.uv.z();
        float minV = textureAtlasPart.getMinV() + v * face.texture.uv.y();
        float maxV = textureAtlasPart.getMinV() + v * face.texture.uv.w();
        Vector3fc from = cube.from;
        Vector3fc to = cube.to;

        Vector3f v1,v2,v3,v4;

        switch (facing) {
            case NORTH:
                v1 = new Vector3f(from.x(), from.y(), to.z());
                v2 = new Vector3f(to.x(), from.y(), to.z());
                v3 = new Vector3f(to.x(), to.y(), to.z());
                v4 = new Vector3f(from.x(), to.y(), to.z());
                break;
            case SOUTH:
                v1 = new Vector3f(to.x(), from.y(), from.z());
                v2 = new Vector3f(from.x(), from.y(), from.z());
                v3 = new Vector3f(from.x(), to.y(), from.z());
                v4 = new Vector3f(to.x(), to.y(), from.z());
                break;
            case EAST:
                v1 = new Vector3f(to.x(), from.y(), to.z());
                v2 = new Vector3f(to.x(), from.y(), from.z());
                v3 = new Vector3f(to.x(), to.y(), from.z());
                v4 = new Vector3f(to.x(), to.y(), to.z());
                break;
            case WEST:
                v1 = new Vector3f(from.x(), from.y(), from.z());
                v2 = new Vector3f(from.x(), from.y(), to.z());
                v3 = new Vector3f(from.x(), to.y(), to.z());
                v4 = new Vector3f(from.x(), to.y(), from.z());
                break;
            case UP:
                v1 = new Vector3f(from.x(), to.y(), to.z());
                v2 = new Vector3f(to.x(), to.y(), to.z());
                v3 = new Vector3f(to.x(), to.y(), from.z());
                v4 = new Vector3f(from.x(), to.y(), from.z());
                break;
            case DOWN:
                v1 = new Vector3f(to.x(), from.y(), to.z());
                v2 = new Vector3f(from.x(), from.y(), to.z());
                v3 = new Vector3f(from.x(), from.y(), from.z());
                v4 = new Vector3f(to.x(), from.y(), from.z());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + facing);
        }
        bakeQuad(buffer,v1,v2,v3,v4, new Vector2f(minU,minV), new Vector2f(maxU,maxV));
    }

    private void bakeQuad(GLBuffer buffer, Vector3fc v1, Vector3fc v2, Vector3fc v3, Vector3fc v4, Vector2fc minUv,Vector2fc maxUv){
        var normal = Math2.calcNormalByVertices(v1,v2,v3);
        var normal1 = Math2.calcNormalByVertices(v1,v3,v4);
        buffer.pos(v1).color(1, 1, 1).uv(minUv.x(), maxUv.y()).normal(normal).endVertex(); // 1
        buffer.pos(v2).color(1, 1, 1).uv(maxUv.x(), maxUv.y()).normal(normal).endVertex(); // 2
        buffer.pos(v3).color(1, 1, 1).uv(maxUv.x(), minUv.y()).normal(normal).endVertex(); // 3

        buffer.pos(v1).color(1, 1, 1).uv(minUv.x(), maxUv.y()).normal(normal1).endVertex(); // 1
        buffer.pos(v3).color(1, 1, 1).uv(maxUv.x(), minUv.y()).normal(normal1).endVertex(); // 3
        buffer.pos(v4).color(1, 1, 1).uv(minUv.x(), minUv.y()).normal(normal1).endVertex(); // 4
    }

    private void bakeQuad(GLBuffer buffer,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float u1, float v1,
                          float u2, float v2) {
        bakeQuad(buffer, new Vector3f(x1,y1,z1),
                new Vector3f(x2,y2,z2),
                new Vector3f(x3,y3,z3),
                new Vector3f(x4,y4,z4),
                new Vector2f(u1,v1),new Vector2f(u2,v2));
    }

    private class BakedModelPrimer {
        public List<Pair<boolean[], GLBuffer>> meshes = new ArrayList<>();

        public GLBuffer getBuffer(boolean[] cullFaces) {
            for (Pair<boolean[], GLBuffer> mesh : meshes) {
                if (Arrays.equals(mesh.getLeft(), cullFaces)) {
                    return mesh.getRight();
                }
            }
            GLBuffer buffer = GLBufferPool.getDefaultHeapBufferPool().get();
            meshes.add(Pair.of(cullFaces, buffer));
            return buffer;
        }

        public Model build() {
            List<Model.Mesh> bakedMeshes = new ArrayList<>();
            for (Pair<boolean[], GLBuffer> mesh : meshes) {
                bakedMeshes.add(new Model.Mesh(mesh.getRight().getBackingBuffer().array(), mesh.getLeft()));
            }
            return new Model(List.copyOf(bakedMeshes));
        }
    }
}

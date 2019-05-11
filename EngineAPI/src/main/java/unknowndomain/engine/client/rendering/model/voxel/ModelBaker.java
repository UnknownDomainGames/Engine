package unknowndomain.engine.client.rendering.model.voxel;

import org.apache.commons.lang3.tuple.Pair;
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

    public BakedModel bake(Model model) {
        BakedModelPrimer primer = new BakedModelPrimer();

        List<Model.Element> elements = model.parent == null ? model.elements : modelManager.getModel(model.parent).elements;
        for (Model.Element element : elements) {
            Model.Element.Cube cube = (Model.Element.Cube) element;

            Model.Element.Cube.Face[] faces = cube.faces;
            for (Facing facing : Facing.values()) {
                Model.Element.Cube.Face face = faces[facing.index];
                if (face == null)
                    continue;

                bakeFace(cube, face, facing, primer.getBuffer(face.cullFace));
            }

        }
        return primer.build();
    }

    private void bakeFace(Model.Element.Cube cube, Model.Element.Cube.Face face, Facing facing, GLBuffer buffer) {
        TextureAtlasPart textureAtlasPart = face.texture.textureAtlasPart;
        float u = textureAtlasPart.getMaxU() - textureAtlasPart.getMinU();
        float v = textureAtlasPart.getMaxV() - textureAtlasPart.getMinV();
        float minU = textureAtlasPart.getMinU() + u * face.texture.uv.x();
        float maxU = textureAtlasPart.getMinU() + u * face.texture.uv.z();
        float minV = textureAtlasPart.getMinV() + v * face.texture.uv.y();
        float maxV = textureAtlasPart.getMinV() + v * face.texture.uv.w();
        Vector3fc from = cube.from;
        Vector3fc to = cube.to;
        float fromX;
        float fromY;
        float toX;
        float toY;
        float z;

        switch (facing) {
            case NORTH:
                fromX = from.x();
                fromY = from.y();
                toX = to.x();
                toY = to.y();
                z = to.z();
                bakeQuad(buffer, fromX, fromY, z, toX, fromY, z, toX, toY, z, fromX, toY, z, minU, minV, maxU, maxV);
                break;
            case SOUTH:
                fromX = from.x();
                fromY = from.y();
                toX = to.x();
                toY = to.y();
                z = from.z();
                bakeQuad(buffer, toX, fromY, z, fromX, fromY, z, fromX, toY, z, toX, toY, z, minU, minV, maxU, maxV);
                break;
            case EAST:
                fromX = from.y();
                fromY = from.z();
                toX = to.y();
                toY = to.z();
                z = to.x();
                bakeQuad(buffer, z, fromX, toY, z, fromX, fromY, z, toX, fromY, z, toX, toY, minU, minV, maxU, maxV);
                break;
            case WEST:
                fromX = from.y();
                fromY = from.z();
                toX = to.y();
                toY = to.z();
                z = from.x();
                bakeQuad(buffer, z, fromX, fromY, z, fromX, toY, z, toX, toY, z, toX, fromY, minU, minV, maxU, maxV);
                break;
            case UP:
                fromX = from.x();
                fromY = from.z();
                toX = to.x();
                toY = to.z();
                z = to.y();
                bakeQuad(buffer, fromX, z, toY, toX, z, toY, toX, z, fromY, fromX, z, fromY, minU, minV, maxU, maxV);
                break;
            case DOWN:
                fromX = from.x();
                fromY = from.z();
                toX = to.x();
                toY = to.z();
                z = from.y();
                bakeQuad(buffer, toX, z, toY, fromX, z, toY, fromX, z, fromY, toX, z, fromY, minU, minV, maxU, maxV);
                break;
        }
    }

    private void bakeQuad(GLBuffer buffer,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float u1, float v1,
                          float u2, float v2) {
        var normal = Math2.calcNormalByVertices(new float[]{
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
        });
        var normal1 = Math2.calcNormalByVertices(new float[]{
                x1, y1, z1,
                x3, y3, z3,
                x4, y4, z4,
        });
        buffer.pos(x1, y1, z1).color(1, 1, 1).uv(u1, v2).normal(normal).endVertex(); // 1
        buffer.pos(x2, y2, z2).color(1, 1, 1).uv(u2, v2).normal(normal).endVertex(); // 2
        buffer.pos(x3, y3, z3).color(1, 1, 1).uv(u2, v1).normal(normal).endVertex(); // 3

        buffer.pos(x1, y1, z1).color(1, 1, 1).uv(u1, v2).normal(normal1).endVertex(); // 1
        buffer.pos(x3, y3, z3).color(1, 1, 1).uv(u2, v1).normal(normal1).endVertex(); // 3
        buffer.pos(x4, y4, z4).color(1, 1, 1).uv(u1, v1).normal(normal1).endVertex(); // 4
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

        public BakedModel build() {
            List<BakedModel.Mesh> bakedMeshes = new ArrayList<>();
            for (Pair<boolean[], GLBuffer> mesh : meshes) {
                bakedMeshes.add(new BakedModel.Mesh(mesh.getRight().getBackingBuffer().array(), mesh.getLeft()));
            }
            return new BakedModel(List.copyOf(bakedMeshes));
        }
    }
}

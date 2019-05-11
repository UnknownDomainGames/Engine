package unknowndomain.engine.client.rendering.model.voxel;

import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelBaker {

    private final VoxelModelManager voxelModelManager;

    public ModelBaker(VoxelModelManager voxelModelManager) {
        this.voxelModelManager = voxelModelManager;
    }

    public BakedModel bake(Model model) {
        BakedModelPrimer primer = new BakedModelPrimer();

        return primer.build();
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

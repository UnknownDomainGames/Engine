package unknowndomain.engine.client.rendering.model.voxel;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.block.BlockMeshGenerator;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

public class VoxelBlockMeshGenerator implements BlockMeshGenerator {

    private final BakedModel model;

    public VoxelBlockMeshGenerator(BakedModel model) {
        this.model = model;
    }

    @Override
    public void generate(ClientBlock block, BlockAccessor world, BlockPos pos, GLBuffer buffer) {
        buffer.posOffset(pos.getX(), pos.getY(), pos.getZ());
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        boolean[] cullFaces = new boolean[6];
        for (Facing facing : Facing.values()) {
            mutablePos.set(pos);
            if (!block.canRenderFace(world, mutablePos, facing)) {
                cullFaces[facing.index] = true;
            }
        }

        for (BakedModel.Mesh mesh : model.getMeshes()) {
            if (!checkCullFaces(cullFaces, mesh.cullFaces)) {
                buffer.put(mesh.data);
            }
        }
    }

    private boolean checkCullFaces(boolean[] cullFaces, boolean[] meshCullFaces) {
        for (int i = 0; i < 6; i++) {
            if (meshCullFaces[i] && !cullFaces[i])
                return false;
        }
        return true;
    }

    @Override
    public void generate(ClientBlock block, GLBuffer buffer) {
        buffer.posOffset(0, 0, 0);
        for (BakedModel.Mesh mesh : model.getMeshes()) {
            buffer.put(mesh.data);
        }
    }
}

package nullengine.client.asset.model.voxel;

import nullengine.Platform;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetPath;
import nullengine.client.asset.AssetTypes;
import nullengine.client.block.ClientBlock;
import nullengine.client.rendering.block.BlockMeshGenerator;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.BlockAccessor;

public class VoxelMeshGenerator implements BlockMeshGenerator {

    public static VoxelMeshGenerator create(AssetPath path) {
        return new VoxelMeshGenerator(Platform.getEngineClient().getAssetManager().create(AssetTypes.VOXEL_MODEL, path));
    }

    private final Asset<VoxelModel> model;

    private VoxelMeshGenerator(Asset<VoxelModel> model) {
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

        VoxelModel voxelModel = this.model.get();
        for (VoxelModel.Mesh mesh : voxelModel.getMeshes()) {
            if (!checkCullFaces(cullFaces, mesh.cullFaces)) {
                for (VoxelModel.Vertex vertex : mesh.vertexes) {
                    buffer.pos(vertex.pos).color(1, 1, 1).uv(vertex.u, vertex.v).normal(vertex.normal).endVertex();
                }
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
        buffer.posOffset(-0.5f,-0.5f,-0.5f);
        VoxelModel voxelModel = this.model.get();
        for (VoxelModel.Mesh mesh : voxelModel.getMeshes()) {
            for (VoxelModel.Vertex vertex : mesh.vertexes) {
                buffer.pos(vertex.pos).color(1, 1, 1).uv(vertex.u, vertex.v).normal(vertex.normal).endVertex();
            }
        }
    }
}

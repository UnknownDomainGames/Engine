package unknowndomain.engine.world.gen;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;
import unknowndomain.engine.world.chunk.ChunkConstants;

public class ChunkGeneratorFlat implements ChunkGenerator {

    private final World world;
    private final Setting setting;


    public ChunkGeneratorFlat(World world, Setting setting){
        this.world = world;
        this.setting = setting;
    }

    @Override
    public void base(Chunk chunk) {
        int cx = chunk.getChunkX();
        int cy = chunk.getChunkY();
        int cz = chunk.getChunkZ();
        for (int j = 0; j < ChunkConstants.SIZE_Y; j++) {
            if(j + cy * ChunkConstants.SIZE_Y >= setting.getLayers().length){
                break;
            }
            for(int i = 0; i < ChunkConstants.SIZE_X; i++){
                for (int k = 0; k < ChunkConstants.SIZE_Z; k++){
                    chunk.setBlock(BlockPos.of(i,j,k),setting.getLayers()[j + cy * ChunkConstants.SIZE_Y],null);
                }
            }
        }
    }

    public static class Setting{
        private Block[] layers;

        public Block[] getLayers() {
            return layers;
        }

        public Setting setLayers(Block[] layers) {
            this.layers = layers;
            return this;
        }
    }
}

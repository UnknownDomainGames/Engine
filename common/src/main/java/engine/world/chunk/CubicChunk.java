package engine.world.chunk;

import engine.block.state.BlockState;
import engine.event.block.cause.BlockChangeCause;
import engine.game.GameServerFullAsync;
import engine.math.BlockPos;
import engine.registry.Registries;
import engine.server.network.packet.s2c.PacketBlockUpdate;
import engine.util.NibbleArray;
import engine.world.World;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static engine.world.chunk.ChunkConstants.*;

public class CubicChunk implements Chunk {

    private final WeakReference<World> world;
    private final ChunkPos pos;

    private final Vector3ic min;
    private final Vector3ic max;
    private final Vector3ic center;

    private BlockStorage blockStorage;
    private int nonAirBlockCount = 0;

    public CubicChunk(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = new WeakReference<>(world);
        this.pos = ChunkPos.of(chunkX, chunkY, chunkZ);
        this.min = new Vector3i(chunkX << CHUNK_X_BITS, chunkY << CHUNK_Y_BITS, chunkZ << CHUNK_Z_BITS);
        this.max = min.add(CHUNK_X_SIZE, CHUNK_Y_SIZE, CHUNK_Z_SIZE, new Vector3i());
        this.center = min.add(CHUNK_X_SIZE >> 1, CHUNK_Y_SIZE >> 1, CHUNK_Z_SIZE >> 1, new Vector3i());
    }

    @Override
    public ChunkStatus getStatus() {
        return null; //TODO
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world.get();
    }

    @Override
    public ChunkPos getPos() {
        return pos;
    }

    @Override
    public int getX() {
        return pos.x();
    }

    @Override
    public int getY() {
        return pos.y();
    }

    @Override
    public int getZ() {
        return pos.z();
    }

    @Nonnull
    @Override
    public Vector3ic getMin() {
        return min;
    }

    @Nonnull
    @Override
    public Vector3ic getMax() {
        return max;
    }

    @Nonnull
    @Override
    public Vector3ic getCenter() {
        return center;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        if (blockStorage == null) {
            return Registries.getBlockRegistry().air().getDefaultState();
        }

        return blockStorage.getBlock(x, y, z);
    }

    @Override
    public BlockState setBlock(@Nonnull BlockPos pos, @Nonnull BlockState block, @Nonnull BlockChangeCause cause) {
        var block1 = setBlock(pos.x(), pos.y(), pos.z(), block);
        var world1 = getWorld();
        if (!(cause instanceof BlockChangeCause.WorldGenCause))
            if (world1.getGame() instanceof GameServerFullAsync) {
                ((GameServerFullAsync) world1.getGame()).getNetworkServer().sendToAll(new PacketBlockUpdate(world1, pos));
            }
        return block1;
    }

    protected BlockState setBlock(int x, int y, int z, BlockState block) {
        if (blockStorage == null) {
            blockStorage = new BlockStorage();
        }

        if (block.getPrototype() != Registries.getBlockRegistry().air()) {
            nonAirBlockCount++;
        } else {
            nonAirBlockCount--;
        }

        var block1 = blockStorage.setBlock(x, y, z, block);
        return block1;
    }

    @Override
    public boolean isAirChunk() {
        return nonAirBlockCount == 0;
    }

    public void write(DataOutput output) throws IOException {
        output.writeShort(nonAirBlockCount);

        if (nonAirBlockCount != 0) {
            var array = blockStorage.getData().toArray();
            // get state ids appeared in the chunk, with most occurrences at index 0
            var ids = Arrays.stream(array).boxed().collect(Collectors.toMap(Function.identity(), d -> 1, Integer::sum))
                    .entrySet().stream()
                    .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                    .map(Map.Entry::getKey).toArray(Integer[]::new);
            // map it into BlockState for palette
            var states = Arrays.stream(ids).map(id -> Registries.getBlockRegistry().getStateFromId(id)).toArray(BlockState[]::new);
            // map state ids to palette index
            var mapping = IntStream.range(0, ids.length).boxed().collect(Collectors.toMap(index -> ids[index], index -> index));
            // save palette
            try (var outputStream = new ByteArrayOutputStream();var writer = new OutputStreamWriter(outputStream)) {
                for (int i = 0; i < states.length; i++) {
                    var str = states[i].toStorageString();
                    writer.write(str.length());
                    writer.write(str);
                }
                writer.flush();
                output.writeInt(outputStream.size());
                output.write(outputStream.toByteArray());
            }
            var nibbleArray = new NibbleArray(blockStorage.getData().getBitsPreEntry(), blockStorage.getData().length());
            for (int i = 0; i < array.length; i++) {
                nibbleArray.set(i, mapping.get(array[i]));
            }
            var data = nibbleArray.getBackingArray();
            for (long datum : data) output.writeLong(datum);
        }
    }

    public void read(DataInput input) throws IOException {
        nonAirBlockCount = input.readShort();

        if (nonAirBlockCount != 0) {
            var paletteSize = input.readInt();
            var bytes = new byte[paletteSize];
            input.readFully(bytes);
            var list = new ArrayList<BlockState>();
            try (var inputStream = new ByteArrayInputStream(bytes);var reader = new InputStreamReader(inputStream)){
                while(true) {
                    var len = reader.read();
                    if(len == -1) break;
                    var buffer = CharBuffer.allocate(len);
                    var read = reader.read(buffer);
                    buffer.flip();
                    var s = buffer.toString();
                    try {
                        list.add(Registries.getBlockRegistry().getValue(BlockState.getBlockNameFromStorageString(s)).getDefaultState().fromStorageString(s));
                    } catch (Exception e) {
                        //TODO: warning
                        list.add(Registries.getBlockRegistry().air().getDefaultState());
                    }
                }
            }


            blockStorage = new BlockStorage();
            long[] data = blockStorage.getData().getBackingArray();
            for (int i = 0; i < data.length; i++) {
                data[i] = input.readLong();
            }
            for (int i = 0; i < blockStorage.getData().length(); i++) {
                blockStorage.getData().set(i, Registries.getBlockRegistry().getStateId(list.get(blockStorage.getData().get(i))));
            }
        }
    }

    public void writeBlockContent(DataOutput output) throws IOException{
        output.writeShort(nonAirBlockCount);

        if (nonAirBlockCount != 0) {
            for (long l : blockStorage.getData().getBackingArray()) {
                output.writeLong(l);
            }
        }
    }

    public void readBlockContent(DataInput input) throws IOException {
        nonAirBlockCount = input.readShort();

        if (nonAirBlockCount != 0) {
            blockStorage = new BlockStorage();
            long[] data = blockStorage.getData().getBackingArray();
            for (int i = 0; i < data.length; i++) {
                data[i] = input.readLong();
            }
        }
    }
}

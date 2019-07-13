package nullengine.world.gen;

import nullengine.world.chunk.Chunk;

public interface ChunkGenerator<T extends ChunkGeneratorSetting> {
    void generate(Chunk chunk, T setting);

    default void generate(Chunk chunk){
        generate(chunk, getSetting());
    }

    T getSetting();

    void setSetting(T setting);
}

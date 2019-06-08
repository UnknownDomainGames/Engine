package unknowndomain.engine.world;

import unknowndomain.engine.game.Game;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.gen.ChunkGenerator;

import java.nio.file.Path;

public class WorldCommonProvider extends BaseWorldProvider<WorldCommon> {

    private ChunkGenerator chunkGenerator;
    private Game currentGame;

    public WorldCommonProvider(){

    }

    @Override
    public WorldCommon create(String name, Path storagePath) {
        if(currentGame == null){
            throw new IllegalStateException("World Provider should be provided Game context in order to provide world!");
        }
        return null;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }

}

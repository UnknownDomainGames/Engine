package engine.world.provider;

import configuration.Config;
import configuration.io.ConfigIOUtils;
import engine.block.state.BlockState;
import engine.game.Game;
import engine.registry.Registries;
import engine.world.BaseWorldProvider;
import engine.world.World;
import engine.world.WorldCommon;
import engine.world.WorldCreationSetting;
import engine.world.gen.FlatChunkGenerator;
import engine.world.impl.FlatWorldCreationSetting;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlatWorldProvider extends BaseWorldProvider {
    @Nonnull
    @Override
    public World create(@Nonnull Game game, @Nonnull Path storagePath, @Nonnull String name, @Nonnull WorldCreationSetting creationSetting) {
        FlatWorldCreationSetting setting = (FlatWorldCreationSetting) creationSetting;
        Config config = new Config();
        config.set("name", name);
        List<String> layers = new ArrayList<>();
        for (BlockState block : setting.getLayers()) {
            layers.add(block.toStorageString());
        }
        config.set("layers", layers);
        config.save(storagePath.resolve("world.json"));
        return new WorldCommon(game, this, storagePath, name, creationSetting, new FlatChunkGenerator(setting.getLayers()));
    }

    @Nonnull
    @Override
    public World load(@Nonnull Game game, @Nonnull Path storagePath) {
        Config config = ConfigIOUtils.load(storagePath.resolve("world.json"));
        String name = config.getString("name");
        List<Object> layers = config.getList("layers", List.of());
        BlockState[] blocks = new BlockState[layers.size()];
        for (int i = 0; i < layers.size(); i++) {
            var s = (String) layers.get(i);
            var block = Registries.getBlockRegistry().getValue(BlockState.getBlockNameFromStorageString(s));
            blocks[i] = block.getDefaultState().fromStorageString(s);
        }
        FlatWorldCreationSetting creationSetting = new FlatWorldCreationSetting();
        creationSetting.layers(blocks);
        return new WorldCommon(game, this, storagePath, name, creationSetting, new FlatChunkGenerator(blocks));
    }
}

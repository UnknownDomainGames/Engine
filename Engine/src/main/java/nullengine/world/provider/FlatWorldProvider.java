package nullengine.world.provider;

import configuration.Config;
import configuration.parser.ConfigParsers;
import nullengine.block.Block;
import nullengine.game.Game;
import nullengine.registry.Registries;
import nullengine.world.BaseWorldProvider;
import nullengine.world.World;
import nullengine.world.WorldCommon;
import nullengine.world.WorldCreationSetting;
import nullengine.world.gen.FlatChunkGenerator;
import nullengine.world.impl.FlatWorldCreationSetting;

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
        for (Block block : setting.getLayers()) {
            layers.add(block.getName().getUniqueName());
        }
        config.set("layers", layers);
        ConfigParsers.save(storagePath.resolve("world.json"), config);
        return new WorldCommon(game, this, storagePath, name, creationSetting, new FlatChunkGenerator(setting.getLayers()));
    }

    @Nonnull
    @Override
    public World load(@Nonnull Game game, @Nonnull Path storagePath) {
        Config config = ConfigParsers.load(storagePath.resolve("world.json"));
        String name = config.getString("name");
        List<Object> layers = config.getList("layers", List.of());
        Block[] blocks = new Block[layers.size()];
        for (int i = 0; i < layers.size(); i++) {
            blocks[i] = Registries.getBlockRegistry().getValue((String) layers.get(i));
        }
        FlatWorldCreationSetting creationSetting = new FlatWorldCreationSetting();
        creationSetting.layers(blocks);
        return new WorldCommon(game, this, storagePath, name, creationSetting, new FlatChunkGenerator(blocks));
    }
}

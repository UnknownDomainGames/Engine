package nullengine.mod.init.handler;

import nullengine.Platform;
import nullengine.client.asset.source.ModAssetSource;
import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationTask;
import nullengine.mod.init.ModInitializer;

import java.io.IOException;

public class AssetTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        try {
            Platform.getEngineClient().getAssetManager().getSourceManager().getSources().addLast(ModAssetSource.create(mod));
        } catch (IOException e) {
            mod.getLogger().warn("Cannot add mod asset.");
        }
    }
}

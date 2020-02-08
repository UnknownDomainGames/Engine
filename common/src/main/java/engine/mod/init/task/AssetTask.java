package engine.mod.init.task;

import engine.Platform;
import engine.client.asset.source.ModAssetSource;
import engine.mod.ModContainer;
import engine.mod.dummy.DummyModContainer;
import engine.mod.init.ModInitializer;

import java.io.IOException;

public class AssetTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        if (mod instanceof DummyModContainer)
            return;
        try {
            Platform.getEngineClient().getAssetManager().getSourceManager().getSources().addLast(ModAssetSource.create(mod));
        } catch (IOException e) {
            mod.getLogger().warn("Cannot add mod asset.");
        }
    }
}

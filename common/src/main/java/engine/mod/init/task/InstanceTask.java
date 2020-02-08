package engine.mod.init.task;

import engine.mod.ModContainer;
import engine.mod.dummy.DummyModContainer;
import engine.mod.init.ModInitializer;

public class InstanceTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        if (mod instanceof DummyModContainer) {
            return;
        }
        mod.getEventBus().register(mod.getInstance());
    }
}

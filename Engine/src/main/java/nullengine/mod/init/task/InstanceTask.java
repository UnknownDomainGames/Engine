package nullengine.mod.init.task;

import nullengine.mod.ModContainer;
import nullengine.mod.dummy.DummyModContainer;
import nullengine.mod.init.ModInitializer;

public class InstanceTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        if (mod instanceof DummyModContainer) {
            return;
        }
        mod.getEventBus().register(mod.getInstance());
    }
}

package nullengine.mod.init.handler;

import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationTask;
import nullengine.mod.init.ModInitializer;

public class InstanceTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().register(mod.getInstance());
    }
}

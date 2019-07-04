package nullengine.mod.init.handler;

import nullengine.event.mod.ModLifecycleEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializationTask;
import nullengine.mod.init.ModInitializer;

public class PostInitializationTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().post(new ModLifecycleEvent.PostInitialization(mod));
    }
}

package engine.mod.init.task;

import engine.event.mod.ModLifecycleEvent;
import engine.mod.ModContainer;
import engine.mod.init.ModInitializer;

public class PreInitializationTask implements ModInitializationTask {
    @Override
    public void run(ModInitializer initializer, ModContainer mod) {
        mod.getEventBus().post(new ModLifecycleEvent.PreInitialization(mod));
    }
}

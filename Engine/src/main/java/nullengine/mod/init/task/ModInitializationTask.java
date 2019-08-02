package nullengine.mod.init.task;

import nullengine.mod.ModContainer;
import nullengine.mod.init.ModInitializer;

public interface ModInitializationTask {

    void run(ModInitializer initializer, ModContainer mod);
}

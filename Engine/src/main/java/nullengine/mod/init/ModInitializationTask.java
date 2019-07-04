package nullengine.mod.init;

import nullengine.mod.ModContainer;

public interface ModInitializationTask {

    void run(ModInitializer initializer, ModContainer mod);
}

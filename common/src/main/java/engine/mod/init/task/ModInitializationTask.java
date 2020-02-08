package engine.mod.init.task;

import engine.mod.ModContainer;
import engine.mod.init.ModInitializer;

public interface ModInitializationTask {

    void run(ModInitializer initializer, ModContainer mod);
}

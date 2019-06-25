package nullengine.mod.init;

import nullengine.mod.ModContainer;

public interface ModInitializationHandler {

    void handle(ModInitializer initializer, ModContainer mod);
}

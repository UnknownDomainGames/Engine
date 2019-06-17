package unknowndomain.engine.mod.init;

import unknowndomain.engine.mod.ModContainer;

public interface ModInitializationHandler {

    void handle(ModInitializer initializer, ModContainer mod);
}

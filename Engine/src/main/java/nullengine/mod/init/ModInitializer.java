package nullengine.mod.init;

import nullengine.Engine;
import nullengine.mod.ModContainer;
import nullengine.mod.init.task.*;
import nullengine.registry.Namespaces;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class ModInitializer {

    private final Engine engine;

    private final LinkedList<Pair<String, ModInitializationTask>> handlers = new LinkedList<>();

    private String stage;

    public ModInitializer(Engine engine) {
        this.engine = engine;

        addLast("Instance", new InstanceTask());
        addLast("Asset", new AssetTask());
        addLast("AutoListen", new AutoListenTask());
        addLast("Injection", new InjectionTask());
        addLast("PreInitialization", new PreInitializationTask());
        addLast("Registration", new RegistrationTask());
        addLast("Initialization", new InitializationTask());
        addLast("PostInitialization", new PostInitializationTask());
    }

    public void addFirst(String name, ModInitializationTask handler) {
        handlers.addFirst(Pair.of(name, handler));
    }

    public void addLast(String name, ModInitializationTask handler) {
        handlers.addLast(Pair.of(name, handler));
    }

    public void addBefore(String name, String nextHandler, ModInitializationTask handler) {
        handlers.add(indexOf(nextHandler), Pair.of(name, handler));
    }

    public void addAfter(String name, String previousHandler, ModInitializationTask handler) {
        handlers.add(indexOf(previousHandler) + 1, Pair.of(name, handler));
    }

    public ModInitializationTask getHandler(String name) {
        for (Pair<String, ModInitializationTask> handler : handlers) {
            if (name.equals(handler.getLeft())) {
                return handler.getRight();
            }
        }
        return null;
    }

    private int indexOf(String name) {
        int i = 0;
        for (Pair<String, ModInitializationTask> handler : handlers) {
            if (name.equals(handler.getLeft())) {
                return i;
            }
            i++;
        }
        throw new NoSuchElementException("Cannot find asset reload handler \"" + name + "\".");
    }

    public void init(ModContainer mod) {
        Namespaces.setNamespace(mod.getId());
        for (Pair<String, ModInitializationTask> handler : handlers) {
            stage = handler.getLeft();
            handler.getRight().run(this, mod);
        }
        Namespaces.setNamespace(null);
    }

    public String getStage() {
        return stage;
    }

    public Engine getEngine() {
        return engine;
    }
}

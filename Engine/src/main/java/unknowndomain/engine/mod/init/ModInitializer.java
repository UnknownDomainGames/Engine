package unknowndomain.engine.mod.init;

import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.Engine;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.init.handler.*;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class ModInitializer {

    private final Engine engine;

    private final LinkedList<Pair<String, ModInitializationHandler>> handlers = new LinkedList<>();

    private String stage;

    public ModInitializer(Engine engine) {
        this.engine = engine;

        addLast("Instance", new InstanceHandler());
        addLast("AutoListen", new AutoListenHandler());
        addLast("PreInitialization", new PreInitializationHandler());
        addLast("Injection", new InjectHandler());
        addLast("Initialization", new InitializationHandler());
        addLast("PostInitialization", new PostInitializationHandler());
    }

    public void addFirst(String name, ModInitializationHandler handler) {
        handlers.addFirst(Pair.of(name, handler));
    }

    public void addLast(String name, ModInitializationHandler handler) {
        handlers.addLast(Pair.of(name, handler));
    }

    public void addBefore(String name, String nextHandler, ModInitializationHandler handler) {
        handlers.add(indexOf(nextHandler), Pair.of(name, handler));
    }

    public void addAfter(String name, String previousHandler, ModInitializationHandler handler) {
        handlers.add(indexOf(previousHandler) + 1, Pair.of(name, handler));
    }

    public ModInitializationHandler getHandler(String name) {
        for (Pair<String, ModInitializationHandler> handler : handlers) {
            if (name.equals(handler.getLeft())) {
                return handler.getRight();
            }
        }
        return null;
    }

    private int indexOf(String name) {
        int i = 0;
        for (Pair<String, ModInitializationHandler> handler : handlers) {
            if (name.equals(handler.getLeft())) {
                return i;
            }
            i++;
        }
        throw new NoSuchElementException("Cannot find asset reload handler \"" + name + "\".");
    }

    public void init(ModContainer mod) {
        for (Pair<String, ModInitializationHandler> handler : handlers) {
            stage = handler.getLeft();
            handler.getRight().handle(this, mod);
        }
    }

    public String getStage() {
        return stage;
    }

    public Engine getEngine() {
        return engine;
    }
}

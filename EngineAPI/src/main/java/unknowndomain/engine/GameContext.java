package unknowndomain.engine;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class GameContext implements EventBus {
    private RegistryManager manager;
    private EventBus bus;
    private ExecutorService executorService;

    public GameContext(RegistryManager manager, EventBus bus) {
        this.manager = manager;
        this.bus = bus;
        // this.nextTick = nextTick;
    }

    public RegistryManager getRegistry() {
        return manager;
    }

    public Registry<Block> getBlockRegistry() {
        return manager.getRegistry(Block.class);
    }

    public Registry<Item> getItemRegistry() {
        return manager.getRegistry(Item.class);
    }

    @Override
    public boolean post(Event event) {
        return bus.post(event);
    }

    @Override
    public void register(Object listener) {
        bus.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        bus.unregister(listener);
    }

    // public void nextTick(Runnable runnable) {
    //     nextTick.add(runnable);
    // }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public <T> CompletableFuture<T> async(Supplier<T> action) {
        return CompletableFuture.supplyAsync(action, executorService);
    }

    public CompletableFuture<Void> async(Runnable action) {
        return CompletableFuture.runAsync(action, executorService);
    }
}

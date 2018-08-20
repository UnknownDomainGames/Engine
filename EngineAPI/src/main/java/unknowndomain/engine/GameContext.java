package unknowndomain.engine;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.RegistryManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class GameContext implements EventBus {
    private RegistryManager manager;
    private EventBus bus;
    private List<Runnable> nextTick;
    private ExecutorService executorService;

    public GameContext(RegistryManager manager, EventBus bus, List<Runnable> nextTick) {
        this.manager = manager;
        this.bus = bus;
        this.nextTick = nextTick;
    }

    public RegistryManager getManager() {
        return manager;
    }

    public IdentifiedRegistry<Block> getBlockRegistry() {
        return (IdentifiedRegistry<Block>) manager.getRegistry(Block.class);
    }

    public IdentifiedRegistry<Item> getItemRegistry() {
        return (IdentifiedRegistry<Item>) manager.getRegistry(Item.class);
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

    public void nextTick(Runnable runnable) {
        nextTick.add(runnable);
    }

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

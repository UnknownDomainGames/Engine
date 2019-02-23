package unknowndomain.engine.game;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.GenericEvent;
import unknowndomain.engine.event.Order;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class GameContext implements EventBus {
    private final EventBus bus;
    private final RegistryManager manager;
    private ExecutorService executorService;

    private final Block blockAir;

    public GameContext(RegistryManager manager, EventBus bus, Block blockAir) {
        this.manager = manager;
        this.bus = bus;
        this.blockAir = blockAir;
        // this.nextTick = nextTick;
    }

    public RegistryManager getRegistryManager() {
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

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {

    }

    @Override
    public <T extends Event> void addListener(Order order, Consumer<T> consumer) {

    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer) {

    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {

    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer) {

    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer) {

    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer) {

    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {

    }

    public Block getBlockAir() {
        return blockAir;
    }

    public int getBlockAirId() {
        return getBlockRegistry().getId(getBlockAir());
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

package unknowndomain.engine.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Scheduler {

    void scheduleTask(Task task);

    Task scheduleTask(Runnable runnable);

    Task scheduleDelayedTask(Runnable runnable, long delay);

    Task scheduleRepeatingTask(Runnable runnable, long delay, long period);

    void scheduleTask(Consumer<Task> consumer);

    void scheduleDelayedTask(Consumer<Task> consumer, long delay);

    <V> Future<V> scheduleTask(Callable<V> callable);

    <V> Future<V> scheduleDelayedTask(Callable<V> callable, long delay);

    <V> Future<V> scheduleRepeatingTask(Callable<V> callable, long delay, long period);

}

package engine.graphics;

public interface RenderScheduler {

    void runTaskNextFrame(Runnable runnable);

    void runTaskEveryFrame(Runnable runnable);

    void cancelTask(Runnable runnable);
}

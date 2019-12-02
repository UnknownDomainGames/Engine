package nullengine.client.rendering.util;

public class FrameTicker {

    private final Runnable runnable;

    private boolean stopped = false;

    private float fps;
    private float tpf;

    public FrameTicker(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * @return Frame per second
     */
    public float getFps() {
        return fps;
    }

    /**
     * @return Time per frame
     */
    public float getTpf() {
        return tpf;
    }

    public void run() {
        long lastTime = System.nanoTime();
        while (!stopped) {
            runnable.run();
            tpf = (System.nanoTime() - lastTime) / 1e9f;
            fps = 1f / tpf;
            lastTime = System.nanoTime();
        }
    }

    public void stop() {
        stopped = true;
    }
}

package unknowndomain.engine.client;

import unknowndomain.engine.client.rendering.RendererGlobal;

import java.util.Deque;
import java.util.concurrent.SynchronousQueue;

public class RenderWorker implements Runnable {
    private RendererGlobal renderer;
    private Deque<Object> messageQuue;


    @Override
    public void run() {

    }
}

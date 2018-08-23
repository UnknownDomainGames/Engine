package unknowndomain.engine.client;

import unknowndomain.engine.client.rendering.RendererContext;

import java.util.Deque;
import java.util.concurrent.SynchronousQueue;

public class RenderWorker implements Runnable {
    private RendererContext renderer;
    private Deque<Object> messageQuue;


    @Override
    public void run() {

    }
}

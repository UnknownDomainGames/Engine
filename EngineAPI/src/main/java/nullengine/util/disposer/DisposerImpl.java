package nullengine.util.disposer;

public class DisposerImpl extends DisposerBase {
    @Override
    public void run() {
        while (true) {
            try {
                DisposablePhantomReference reference = (DisposablePhantomReference) queue.remove(1L);
                if (reference == null) {
                    break;
                }
                reference.dispose();
                reference.clear();
            } catch (InterruptedException ignored) {
            }
        }
    }
}

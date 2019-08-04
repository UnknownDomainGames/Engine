package nullengine.client.asset.reloading;

import java.util.concurrent.Executor;

public interface AssetReloadScheduler extends Executor {

    void awaitCompletion() throws InterruptedException;
}

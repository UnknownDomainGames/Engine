package nullengine.client.asset;

import nullengine.client.asset.reloading.AssetReloadListener;
import nullengine.client.asset.reloading.AssetReloadManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class AssetReloadManagerImpl implements AssetReloadManager {

    private final AssetReloadSchedulerImpl scheduler = new AssetReloadSchedulerImpl();
    private final LinkedList<Pair<String, AssetReloadListener>> listeners = new LinkedList<>();

    @Override
    public void addFirst(String name, AssetReloadListener listener) {
        listeners.addFirst(Pair.of(name, listener));
    }

    @Override
    public void addLast(String name, AssetReloadListener listener) {
        listeners.addLast(Pair.of(name, listener));
    }

    @Override
    public void addBefore(String name, String nextListener, AssetReloadListener listener) {
        listeners.add(indexOf(nextListener), Pair.of(name, listener));
    }

    @Override
    public void addAfter(String name, String previousListener, AssetReloadListener listener) {
        listeners.add(indexOf(previousListener) + 1, Pair.of(name, listener));
    }

    private int indexOf(String name) {
        int i = 0;
        for (Pair<String, AssetReloadListener> listener : listeners) {
            if (name.equals(listener.getLeft())) {
                return i;
            }
            i++;
        }
        throw new NoSuchElementException("Cannot find asset reload listener \"" + name + "\".");
    }

    @Override
    public void reload() throws InterruptedException {
        listeners.forEach(pair -> pair.getRight().onReload(scheduler));
        scheduler.awaitCompletion();
    }

    public void dispose() {
        scheduler.dispose();
    }
}

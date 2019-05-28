package unknowndomain.engine.client.asset;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class DefaultAssetReloadDispatcher implements AssetReloadDispatcher {

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

    private boolean has(String name) {
        for (Pair<String, AssetReloadListener> listener : listeners) {
            if (name.equals(listener.getLeft())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispatchReload() {
        listeners.forEach(pair -> pair.getRight().onReload());
    }
}

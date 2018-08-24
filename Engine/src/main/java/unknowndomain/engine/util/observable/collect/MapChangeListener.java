package unknowndomain.engine.util.observable.collect;

@FunctionalInterface
public abstract interface MapChangeListener<K, V> {
  public abstract void onChanged(Change<? extends K, ? extends V> paramChange);
  
  public static abstract class Change<K, V> {
    private final ObservableMap<K, V> map;
    
    public Change(ObservableMap<K, V> paramObservableMap) {
      this.map = paramObservableMap;
    }
    
    public ObservableMap<K, V> getMap() {
      return this.map;
    }
    
    public abstract boolean wasAdded();
    
    public abstract boolean wasRemoved();
    
    public abstract K getKey();
    
    public abstract V getValueAdded();
    
    public abstract V getValueRemoved();
  }
}
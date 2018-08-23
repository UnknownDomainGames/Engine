package unknowndomain.engine.util.observable.collect;

import java.util.Map;

import unknowndomain.engine.util.observable.Observable;

public abstract interface ObservableMap<K, V> extends Map<K, V>, Observable {
	
  public abstract void addListener(MapChangeListener<? super K, ? super V> paramMapChangeListener);
  
  public abstract void removeListener(MapChangeListener<? super K, ? super V> paramMapChangeListener);
  
}

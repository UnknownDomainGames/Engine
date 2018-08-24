package unknowndomain.engine.util.observable.collect;

import java.util.Set;

import unknowndomain.engine.util.observable.Observable;

public abstract interface ObservableSet<E> extends Set<E>, Observable {
	
	public abstract void addListener(SetChangeListener<? super E> paramSetChangeListener);

	public abstract void removeListener(SetChangeListener<? super E> paramSetChangeListener);

}

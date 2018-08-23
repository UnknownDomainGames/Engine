package unknowndomain.engine.util.observable.collect;

import java.util.Collection;
import java.util.List;

import unknowndomain.engine.util.observable.Observable;

public interface ObservableList<E> extends List<E>, Observable {
	public abstract void addListener(ListChangeListener<? super E> paramListChangeListener);
	
	public abstract void removeListener(ListChangeListener<? super E> paramListChangeListener);
	
	public abstract boolean addAll(E... paramVarArgs);
	
	public abstract boolean setAll(E... paramVarArgs);
	
	public abstract boolean setAll(Collection<? extends E> paramCollection);

	public abstract boolean removeAll(E... paramVarArgs);
	
	public abstract boolean retainAll(E... paramVarArgs);
	
	public abstract void remove(int paramInt1, int paramInt2);
	
}

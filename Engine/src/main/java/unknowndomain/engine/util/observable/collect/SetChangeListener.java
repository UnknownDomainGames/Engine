package unknowndomain.engine.util.observable.collect;

@FunctionalInterface
public abstract interface SetChangeListener<E> {
  public abstract void onChanged(Change<? extends E> paramChange);
  
  public static abstract class Change<E> {
    private ObservableSet<E> set;
    
    public Change(ObservableSet<E> paramObservableSet) {
      this.set = paramObservableSet;
    }
    
    public ObservableSet<E> getSet() {
      return this.set;
    }
    
    public abstract boolean wasAdded();
    
    public abstract boolean wasRemoved();
    
    public abstract E getElementAdded();
    
    public abstract E getElementRemoved();
  }
}

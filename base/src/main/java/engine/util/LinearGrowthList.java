package engine.util;

import java.util.*;
import java.util.function.Consumer;

public class LinearGrowthList<E> extends AbstractList<E> implements RandomAccess {
    private Object myElem;
    private int mySize;

    public LinearGrowthList() {
    }

    public LinearGrowthList(E element) {
        myElem = element;
        mySize = 1;
    }

    public LinearGrowthList(Collection<? extends E> elements) {
        int size = elements.size();
        if (size == 1) {
            myElem = elements instanceof RandomAccess ? ((List<? extends E>) elements).get(0) : elements.iterator().next();
            mySize = 1;
        } else if (size > 0) {
            myElem = elements.toArray(new Object[size]);
            mySize = size;
        }
    }

    public LinearGrowthList(E... elements) {
        int length = elements.length;
        if (length == 1) {
            myElem = elements[0];
            mySize = 1;
        } else if (length > 0) {
            myElem = Arrays.copyOf(elements, length);
            mySize = length;
        }
    }

    @Override
    public E get(int index) {
        int size = mySize;
        rangeCheck(index, size);
        if (size == 1) {
            return asElement();
        } else {
            return asArray()[index];
        }
    }

    @Override
    public int size() {
        return mySize;
    }

    @Override
    public E set(int index, E element) {
        int size = mySize;
        rangeCheck(index, size);
        final E old;
        if (size == 1) {
            old = asElement();
            myElem = element;
        } else {
            E[] array = asArray();
            old = array[index];
            array[index] = element;
        }
        return old;
    }

    @Override
    public boolean add(E element) {
        int size = mySize;
        switch (size) {
            case 0:
                myElem = element;
                break;
            case 1:
                myElem = new Object[]{myElem, element};
                break;
            default:
                E[] array = asArray();
                myElem = Arrays.copyOf(array, size + 1);
                array[size] = element;
                break;
        }
        mySize++;
        modCount++;
        return true;
    }

    @Override
    public void add(int index, E element) {
        int size = mySize;
        rangeCheckForAdd(index, size);
        switch (size) {
            case 0:
                myElem = element;
                break;
            case 1:
                myElem = index == 0 ? new Object[]{element, myElem} : new Object[]{myElem, element};
                break;
            default:
                E[] array = asArray();
                myElem = Arrays.copyOf(array, size + 1);
                int moved = size - index;
                if (moved > 0) {
                    System.arraycopy(array, index, array, index + 1, moved);
                }
                array[index] = element;
                break;
        }
        mySize++;
        modCount++;
    }

    @Override
    public E remove(int index) {
        int size = mySize;
        rangeCheck(index, size);
        final E old;
        switch (size) {
            case 0:
            case 1:
                old = asElement();
                myElem = null;
                break;
            case 2:
                E[] array = asArray();
                old = array[index];
                myElem = array[index ^ 1];
                break;
            default:
                array = asArray();
                old = array[index];
                int moved = size - index - 1;
                if (moved > 0) {
                    System.arraycopy(array, index + 1, array, index, moved);
                }
                array[size - 1] = null;
                break;
        }
        mySize--;
        modCount++;
        return old;
    }

    @Override
    public void clear() {
        myElem = null;
        mySize = 0;
        modCount++;
    }

    @Override
    public Iterator<E> iterator() {
        return mySize == 0 ? Collections.emptyIterator() : super.iterator();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        if (mySize >= 2) {
            Arrays.sort(asArray(), 0, mySize, c);
        }
    }

    @Override
    public Object[] toArray() {
        int size = mySize;
        switch (size) {
            case 0:
                return new Object[0];
            case 1:
                return new Object[]{asElement()};
            default:
                return Arrays.copyOf(asArray(), size);
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int length = a.length;
        int size = mySize;
        switch (size) {
            case 0:
                break;
            case 1:
                //noinspection unchecked
                T t = (T) myElem;
                if (length == 0) {
                    a = Arrays.copyOf(a, 1);
                    a[0] = t;
                    return a;
                }
                a[0] = t;
                break;
            default:
                E[] array = asArray();
                if (length < size) {
                    //noinspection unchecked
                    return (T[]) Arrays.copyOf(array, size, a.getClass());
                }
                System.arraycopy(array, 0, a, 0, size);
        }
        if (length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public int indexOf(Object o) {
        int size = mySize;
        switch (size) {
            case 0:
                return -1;
            case 1:
                return Objects.equals(o, myElem) ? 0 : -1;
            default:
                E[] array = asArray();
                for (int i = 0; i < size; i++) {
                    if (Objects.equals(o, array[i])) {
                        return i;
                    }
                }
                return -1;
        }
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        int size = mySize;
        switch (size) {
            case 0:
                break;
            case 1:
                action.accept(asElement());
                break;
            default:
                E[] array = asArray();
                for (int i = 0; i < size; i++) {
                    action.accept(array[i]);
                }
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof List)) {
            return false;
        }

        List<?> that = (List<?>) o;

        int size = mySize;
        if (size != that.size()) {
            return false;
        }

        return that instanceof RandomAccess ? equalsWithRandomAccessList(size, that) : equalsWithList(size, that);
    }

    private boolean equalsWithRandomAccessList(int size, List<?> that) {
        switch (size) {
            case 0:
                return true;
            case 1:
                return Objects.equals(myElem, that.get(0));
            default:
                return equalsWithList(size, that);
        }
    }

    private boolean equalsWithList(int size, List<?> that) {
        E[] array = asArray();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(array[i], that.get(i))) {
                return false;
            }
        }
        return true;
    }

    private E asElement() {
        //noinspection unchecked
        return (E) myElem;
    }

    private E[] asArray() {
        //noinspection unchecked
        return (E[]) myElem;
    }

    private static void rangeCheck(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private static void rangeCheckForAdd(int index, int size) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}

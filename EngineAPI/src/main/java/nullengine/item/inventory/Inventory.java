package nullengine.item.inventory;

import nullengine.item.Item;
import nullengine.item.ItemStack;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Inventory extends Iterable<ItemStack> {

    Optional<Object> getOwner();

    ItemStack get(int index);

    default ItemStack get(int row, int column) {
        if (row < 0 || row > rowSize())
            throw new IllegalArgumentException(String.format("The row is not in the specified inclusive range of 1 to %d", rowSize()));

        if (column < 0 || column > columnSize())
            throw new IllegalArgumentException(String.format("The column is not in the specified inclusive range of 1 to %d", columnSize()));

        return get(row * columnSize() + column);
    }

    void set(int index, ItemStack itemStack);

    default void set(int row, int column, ItemStack itemStack) {
        if (row < 0 || row > rowSize())
            throw new IllegalArgumentException(String.format("The row is not in the specified inclusive range of 1 to %d", rowSize()));

        if (column < 0 || column > columnSize())
            throw new IllegalArgumentException(String.format("The column is not in the specified inclusive range of 1 to %d", columnSize()));


        set(row * columnSize() + column, itemStack);
    }

    default boolean add(ItemStack itemStack) {
        return add(itemStack.getItem(), itemStack.getAmount());
    }

    boolean add(Item item, int amount);

    default boolean has(ItemStack itemStack) {
        return has(itemStack.getItem(), itemStack.getAmount());
    }

    boolean has(Item item, int amount);

    default boolean take(ItemStack itemStack) {
        return take(itemStack.getItem(), itemStack.getAmount());
    }

    boolean take(Item item, int amount);

    default int count(Item item) {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            ItemStack itemStack = get(i);
            if (itemStack.isItemEquals(item)) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }

    void clear();

    int size();

    int rowSize();

    int columnSize();

    default Spliterator<ItemStack> spliterator() {
        return Spliterators.spliterator(iterator(), size(), 0);
    }

    default Stream<ItemStack> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<ItemStack> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}

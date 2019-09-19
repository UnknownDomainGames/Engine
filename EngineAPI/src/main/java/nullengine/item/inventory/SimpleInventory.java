package nullengine.item.inventory;

import nullengine.item.Item;
import nullengine.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class SimpleInventory implements Inventory {

    private final Optional<Object> owner;
    private final int size;
    private final int rowSize;
    private final int columnSize;
    private final ItemStack[] itemStacks;

    public SimpleInventory(Object owner, int rowSize, int columnSize) {
        this.owner = Optional.ofNullable(owner);
        this.size = rowSize * columnSize;
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.itemStacks = new ItemStack[size];
        Arrays.fill(itemStacks, ItemStack.EMPTY);
    }

    @Override
    public Optional<Object> getOwner() {
        return owner;
    }

    @Override
    public ItemStack get(int index) {
        return itemStacks[index];
    }

    @Override
    public void set(int index, ItemStack itemStack) {
        itemStacks[index] = itemStack;
    }

    @Override
    public boolean add(Item item, int amount) {
        return false;
    }

    @Override
    public boolean has(Item item, int amount) {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            ItemStack itemStack = itemStacks[i];
            if (itemStack.isItemEquals(item)) {
                count += itemStack.getAmount();
                if (count >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean take(Item item, int amount) {
        return false;
    }

    @Override
    public void clear() {
        Arrays.fill(itemStacks, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int rowSize() {
        return rowSize;
    }

    @Override
    public int columnSize() {
        return columnSize;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public ItemStack next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return itemStacks[i++];
            }

            @Override
            public void forEachRemaining(Consumer<? super ItemStack> action) {
                Objects.requireNonNull(action);
                for (; i < size; i++) {
                    action.accept(itemStacks[i]);
                }
            }
        };
    }
}

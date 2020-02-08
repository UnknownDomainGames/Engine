package engine.item.inventory;

import engine.item.Item;
import engine.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

import static engine.item.ItemStack.EMPTY;

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
        Arrays.fill(itemStacks, EMPTY);
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
        List<Integer> foundMatchedSlots = new ArrayList<>();
        List<Integer> foundEmptySlots = new ArrayList<>();
        int foundItemAmount = 0;
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = get(i);
            if (itemStack == EMPTY) {
                foundEmptySlots.add(i);
            } else if (itemStack.isItemEquals(item)) {
                foundMatchedSlots.add(i);
                foundItemAmount += itemStack.getAmount();
            }
        }
        // TODO: add item
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
        List<Integer> foundMatchedSlots = new ArrayList<>();
        int foundItemAmount = 0;
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = get(i);
            if (itemStack.isItemEquals(item)) {
                foundMatchedSlots.add(i);
                foundItemAmount += itemStack.getAmount();
                if (foundItemAmount >= amount) {
                    take(foundMatchedSlots, amount);
                    return true;
                }
            }
        }
        return false;
    }

    private void take(List<Integer> indexes, int amount) {
        for (Integer index : indexes) {
            ItemStack itemStack = get(index);
            if (itemStack.getAmount() <= amount) {
                set(index, EMPTY);
                amount -= itemStack.getAmount();
            } else {
                itemStack.setAmount(itemStack.getAmount() - amount);
            }
        }
    }

    @Override
    public void clear() {
        Arrays.fill(itemStacks, EMPTY);
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

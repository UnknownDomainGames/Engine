package engine.entity.component;

import com.google.common.collect.Lists;
import engine.component.SerializableComponent;
import engine.item.ItemStack;
import engine.math.Math2;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemStorable implements SerializableComponent<ItemStorable> {

    private final ItemStack[] storage;
    private final int maximum;

    public ItemStorable(int maximum) {
        storage = new ItemStack[maximum];
        Arrays.fill(storage, ItemStack.EMPTY);
        this.maximum = maximum;
    }

    public int getStorageCapacity() {
        return maximum;
    }

    public ItemStack setItemStack(ItemStack newStack, int location) {
        Objects.requireNonNull(newStack);
        if (Math2.clamp(location, 0, maximum - 1) != location) {
            throw new IndexOutOfBoundsException(String.format("Item location is out of bound. Bound:0-%d Index:%d", maximum - 1, location));
        }
        ItemStack old = storage[location];
        storage[location] = newStack;
        return old;
    }

    public ItemStack emptyStack(int location) {
        return setItemStack(ItemStack.EMPTY, location);
    }

    public List<ItemStack> emptyAll() {
        var old = Lists.newArrayList(storage);
        Arrays.fill(storage, ItemStack.EMPTY);
        old.removeIf(ItemStack::isEmpty);
        return old;
    }

    @Override
    public String serialKey() {
        return "inventory";
    }

    @Override
    public ItemStorable load(ByteBuffer buffer) {
        return null;
    }

    @Override
    public void save(ByteBuffer buffer) {

    }
}

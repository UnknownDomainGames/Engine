package nullengine.item;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemStack implements Cloneable {

    public static final ItemStack EMPTY = new ItemStack();

    private final Item item;

    private int amount;

    public ItemStack(@Nonnull Item item) {
        this(item, 1);
    }

    public ItemStack(@Nonnull Item item, int amount) {
        this.item = Objects.requireNonNull(item);
        this.amount = amount;
    }

    private ItemStack() {
        this.item = null;
        this.amount = 0;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isEmpty() {
        return item == null || amount <= 0;
    }

    public void ifEmpty(Consumer<ItemStack> consumer) {
        if (isEmpty()) {
            consumer.accept(this);
        }
    }

    public void ifNonEmpty(Consumer<ItemStack> consumer) {
        if (!isEmpty()) {
            consumer.accept(this);
        }
    }

    @Override
    protected Object clone() {
        try {
            ItemStack itemStack = (ItemStack) super.clone();
            itemStack.setAmount(amount);
            return itemStack;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

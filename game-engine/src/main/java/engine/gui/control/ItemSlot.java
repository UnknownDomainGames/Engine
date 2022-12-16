package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.item.ItemStack;

public class ItemSlot extends Button {
    private final MutableObjectValue<ItemStack> itemstack = new SimpleMutableObjectValue<>();
    private ItemView itemView;

    public ItemSlot() {
        itemView = new ItemView();
        this.getChildren().add(itemView);
        itemstack.addChangeListener((observable, oldValue, newValue) -> itemView.itemStack().set(newValue));
    }

    public ItemSlot(ItemStack stack) {
        this();
        itemstack.set(stack);
    }

    public MutableObjectValue<ItemStack> itemStack() {
        return itemstack;
    }

    public MutableDoubleValue slotLength() {
        return itemView.size();
    }

    @Override
    public double computeHeight() {
        return slotLength().get();
    }

    @Override
    public double computeWidth() {
        return slotLength().get();
    }
}

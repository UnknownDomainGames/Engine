package nullengine.client.gui.control;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.item.ItemStack;

public class ItemSlot extends Button {
    private final MutableObjectValue<ItemStack> itemstack = new SimpleMutableObjectValue<>();
    private ItemView itemView;

    public ItemSlot() {
        itemView = new ItemView();
        this.getChildren().add(itemView);
        itemstack.addChangeListener((observable, oldValue, newValue) -> itemView.item().setValue(newValue));
    }

    public ItemSlot(ItemStack stack) {
        this();
        itemstack.setValue(stack);
    }

    public MutableObjectValue<ItemStack> itemStack() {
        return itemstack;
    }

    public MutableFloatValue slotLength() {
        return itemView.viewSize();
    }

    @Override
    public float computeHeight() {
        return slotLength().get();
    }

    @Override
    public float computeWidth() {
        return slotLength().get();
    }
}

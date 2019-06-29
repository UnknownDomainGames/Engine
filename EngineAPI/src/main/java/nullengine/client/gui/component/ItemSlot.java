package nullengine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import nullengine.item.ItemStack;

public class ItemSlot extends Button {
    private final MutableValue<ItemStack> itemstack = new SimpleMutableObjectValue<>();
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

    public MutableValue<ItemStack> itemStack() {
        return itemstack;
    }

    public MutableFloatValue slotLength() {
        return itemView.viewSize();
    }

    @Override
    public float prefHeight() {
        return slotLength().get();
    }

    @Override
    public float prefWidth() {
        return slotLength().get();
    }
}

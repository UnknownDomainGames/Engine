package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.item.ItemStack;

public class ItemSlot extends Button {
    private final MutableValue<ItemStack> itemstack = new SimpleMutableObjectValue<>();
    private TextureItem textureItem;

    public ItemSlot(){
        textureItem = new TextureItem();
        this.getChildren().add(textureItem);
        itemstack.addChangeListener((observable, oldValue, newValue) -> textureItem.item().setValue(newValue));
    }

    public ItemSlot(ItemStack stack){
        this();
        itemstack.setValue(stack);
    }

    public MutableValue<ItemStack> itemStack() {
        return itemstack;
    }

    public MutableFloatValue slotLength(){
        return textureItem.imgLength();
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

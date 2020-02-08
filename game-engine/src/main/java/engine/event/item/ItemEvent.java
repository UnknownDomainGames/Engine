package engine.event.item;

import engine.event.Event;
import engine.item.ItemStack;

public abstract class ItemEvent implements Event {

    private final ItemStack itemStack;

    public ItemEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

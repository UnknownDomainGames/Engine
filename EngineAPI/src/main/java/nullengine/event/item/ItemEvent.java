package nullengine.event.item;

import nullengine.event.Event;
import nullengine.item.ItemStack;

public abstract class ItemEvent implements Event {

    private final ItemStack itemStack;

    public ItemEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

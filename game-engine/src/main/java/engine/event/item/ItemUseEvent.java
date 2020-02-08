package engine.event.item;

import engine.item.ItemStack;

public abstract class ItemUseEvent extends ItemEvent {

    private final int originalDuration;

    private int duration;

    public ItemUseEvent(ItemStack itemStack, int originalDuration, int duration) {
        super(itemStack);
        this.originalDuration = originalDuration;
        this.duration = duration;
    }

    public int getOriginalDuration() {
        return originalDuration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static final class Start extends ItemUseEvent {

        public Start(ItemStack itemStack) {
            super(itemStack, 0, 0);
        }
    }

    public static final class Tick extends ItemUseEvent {

        public Tick(ItemStack itemStack, int originalDuration, int duration) {
            super(itemStack, originalDuration, duration);
        }
    }

    public static final class Stop extends ItemUseEvent {

        public Stop(ItemStack itemStack, int originalDuration, int duration) {
            super(itemStack, originalDuration, duration);
        }
    }

    public static final class Finish extends ItemUseEvent {

        public Finish(ItemStack itemStack, int originalDuration, int duration) {
            super(itemStack, originalDuration, duration);
        }
    }
}

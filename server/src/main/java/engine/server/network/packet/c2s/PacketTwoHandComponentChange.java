package engine.server.network.packet.c2s;

import engine.item.ItemStack;
import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;

import java.io.IOException;

public class PacketTwoHandComponentChange implements Packet {

    private ItemStack mainHand;
    private boolean mainHandChanged = false;
    private ItemStack offHand;
    private boolean offHandChanged = false;

    public PacketTwoHandComponentChange() {
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeBoolean(mainHandChanged);
        if (mainHandChanged) {
            buf.writeItemStack(mainHand);
        }
        buf.writeBoolean(offHandChanged);
        if (offHandChanged) {
            buf.writeItemStack(offHand);
        }
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        mainHandChanged = buf.readBoolean();
        if (mainHandChanged) {
            mainHand = buf.readItemStack();
        }
        offHandChanged = buf.readBoolean();
        if (offHandChanged) {
            offHand = buf.readItemStack();
        }
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public boolean isMainHandChanged() {
        return mainHandChanged;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public boolean isOffHandChanged() {
        return offHandChanged;
    }

    public static class Builder {
        private ItemStack mainHand;
        private boolean mainHandChanged;
        private ItemStack offHand;
        private boolean offHandChanged;

        public Builder() {
        }

        public Builder setMainHand(ItemStack stack) {
            mainHand = stack.clone();
            mainHandChanged = true;
            return this;
        }

        public Builder setOffHand(ItemStack stack) {
            offHand = stack.clone();
            offHandChanged = true;
            return this;
        }

        public Builder restoreMainHand() {
            mainHandChanged = false;
            return this;
        }

        public Builder restoreOffHand() {
            offHandChanged = false;
            return this;
        }

        public PacketTwoHandComponentChange build() {
            var packet = new PacketTwoHandComponentChange();
            if (mainHandChanged) {
                packet.mainHand = mainHand;
                packet.mainHandChanged = true;
            }
            if (offHandChanged) {
                packet.offHand = offHand;
                packet.offHandChanged = true;
            }
            return packet;
        }
    }
}

package unknowndomain.engine.entity;

import com.google.common.collect.ImmutableMap;
import unknowndomain.engine.item.Item;

public class EntityPlayer extends EntityBase {

    public EntityPlayer(int id, ImmutableMap<String, Object> behaviors) {
        super(id, behaviors);
    }

    public static class TwoHandImpl implements TwoHands {
        private Item mainHand, offHand;

        public Item getMainHand() {
            return mainHand;
        }

        @Override
        public void setMainHand(Item mainHand) {
            this.mainHand = mainHand;
        }

        @Override
        public Item getOffHand() {
            return offHand;
        }

        @Override
        public void setOffHand(Item offHand) {
            this.offHand = offHand;
        }
    }
}

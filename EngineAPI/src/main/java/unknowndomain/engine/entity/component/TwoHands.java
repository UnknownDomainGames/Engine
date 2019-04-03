package unknowndomain.engine.entity.component;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.item.Item;

public interface TwoHands extends Component {

    Item getMainHand();

    void setMainHand(Item mainHand);

    Item getOffHand();

    void setOffHand(Item offHand);

    class Impl implements TwoHands {
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

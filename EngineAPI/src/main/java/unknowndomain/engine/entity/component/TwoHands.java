package unknowndomain.engine.entity.component;

import unknowndomain.engine.item.Item;

public interface TwoHands {

    Item getMainHand();

    void setMainHand(Item mainHand);

    Item getOffHand();

    void setOffHand(Item offHand);
}

package unknowndomain.engine.entity;

import unknowndomain.engine.item.Item;

public interface TwoHands {

    Item getMainHand();

    void setMainHand(Item mainHand);

    Item getOffHand();

    void setOffHand(Item offHand);
}

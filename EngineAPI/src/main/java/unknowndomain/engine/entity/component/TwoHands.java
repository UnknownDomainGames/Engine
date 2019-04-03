package unknowndomain.engine.entity.component;

import unknowndomain.engine.component.Component;
import unknowndomain.engine.item.Item;

public interface TwoHands extends Component {

    Item getMainHand();

    void setMainHand(Item mainHand);

    Item getOffHand();

    void setOffHand(Item offHand);
}

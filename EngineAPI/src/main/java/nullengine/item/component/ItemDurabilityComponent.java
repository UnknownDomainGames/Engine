package nullengine.item.component;

import nullengine.component.Component;

public interface ItemDurabilityComponent extends Component {
	int durablity=-1;

	void setDurability(int durability);

	int getDurability(int durability);
}

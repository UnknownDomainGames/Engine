package nullengine.item.component;

import nullengine.component.Component;

public interface ItemHarmComponent extends Component {
	int harm=0;

	void setHarm(int harm);

	int getHarm();

	default void setHeavyHarm(int harm){

	}

	default int getHeavyHarm(){
		return 0;
	}
}

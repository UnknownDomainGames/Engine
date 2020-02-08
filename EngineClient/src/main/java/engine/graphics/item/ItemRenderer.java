package engine.graphics.item;

import engine.component.Component;
import engine.item.ItemStack;

public interface ItemRenderer extends Component {

    void init();

    void render(ItemStack itemStack, float partial);

    void dispose();
}

package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderContext;
import nullengine.component.Component;
import nullengine.item.ItemStack;

public interface ItemRenderer extends Component {

    void init(RenderContext context);

    void render(ItemStack itemStack, float partial);

    void dispose();
}

package nullengine.client.rendering.item;

import nullengine.client.rendering.RenderManager;
import nullengine.component.Component;
import nullengine.component.Owner;
import nullengine.item.ItemStack;

@Owner(RenderManager.class)
public interface ItemRenderManager extends Component {
    void render(ItemStack itemStack, float partial);
}

package engine.graphics.item;

import engine.component.Component;
import engine.graphics.vertex.VertexDataBuf;
import engine.item.ItemStack;

public interface ItemRenderer extends Component {

    void init();

    void generateMesh(VertexDataBuf buffer, ItemStack itemStack, float partial);

    void dispose();
}

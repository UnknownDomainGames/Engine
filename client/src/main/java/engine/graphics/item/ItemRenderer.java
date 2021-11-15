package engine.graphics.item;

import engine.component.Component;
import engine.graphics.vertex.VertexDataBuffer;
import engine.item.ItemStack;

public interface ItemRenderer extends Component {

    void init();

    void generateMesh(VertexDataBuffer buffer, ItemStack itemStack, float partial);

    void dispose();
}

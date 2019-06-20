package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.gui.rendering.TextureItemRenderer;
import unknowndomain.engine.item.ItemStack;

public class TextureItem extends Component {
    private final SimpleMutableObjectValue<ItemStack> item = new SimpleMutableObjectValue<>();
    private final SimpleMutableFloatValue imgX = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imgY = new SimpleMutableFloatValue();
    private final SimpleMutableFloatValue imgLength = new SimpleMutableFloatValue(80);

    public TextureItem(){
        imgX.addChangeListener((ob, o, n) -> requestParentLayout());
        imgY.addChangeListener((ob, o, n) -> requestParentLayout());
        imgLength.addChangeListener((ob, o, n) -> requestParentLayout());
    }

    public TextureItem(ItemStack item){
        this();
        this.item.setValue(item);
    }

    @Override
    public float prefWidth() {
        return imgLength.get();
    }

    @Override
    public float prefHeight() {
        return imgLength.get();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return TextureItemRenderer.INSTANCE;
    }

    public SimpleMutableObjectValue<ItemStack> item(){
        return item;
    }

    public SimpleMutableFloatValue imgX() {
        return imgX;
    }

    public SimpleMutableFloatValue imgY() {
        return imgY;
    }

    public SimpleMutableFloatValue imgLength() {
        return imgLength;
    }
}

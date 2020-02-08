package engine.gui.control;

import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.ItemViewRenderer;
import engine.item.ItemStack;

public class ItemView extends Node {
    private final SimpleMutableObjectValue<ItemStack> item = new SimpleMutableObjectValue<>();
    private final SimpleMutableFloatValue viewSize = new SimpleMutableFloatValue(80);

    public ItemView() {
        viewSize.addChangeListener((ob, o, n) -> requestParentLayout());
    }

    public ItemView(ItemStack item) {
        this();
        this.item.setValue(item);
    }

    @Override
    public float prefWidth() {
        return viewSize.get();
    }

    @Override
    public float prefHeight() {
        return viewSize.get();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return ItemViewRenderer.INSTANCE;
    }

    public SimpleMutableObjectValue<ItemStack> item() {
        return item;
    }

    public SimpleMutableFloatValue viewSize() {
        return viewSize;
    }
}

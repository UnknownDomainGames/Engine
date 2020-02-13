package engine.gui.rendering;

import engine.graphics.item.ItemRenderManager;
import engine.gui.control.ItemView;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ItemViewRenderer implements ComponentRenderer<ItemView> {

    public static final ItemViewRenderer INSTANCE = new ItemViewRenderer();

    @Override
    public void render(ItemView component, Graphics graphics) {
        component.item().ifPresent(itemStack -> {
            graphics.enableGamma();
            graphics.pushModelMatrix(new Matrix4f().translationRotateScale(
                    new Vector3f(component.viewSize().get() * 0.5f, component.viewSize().get() * 0.5f, 0),
                    new Quaternionf().rotateAxis((float) -(Math.PI / 4), 0, 1, 0).rotateAxis((float) -Math.PI / 6f, 1, 0, -1),
                    new Vector3f(component.viewSize().get() * 0.6f, -component.viewSize().get() * 0.6f, component.viewSize().get() * 0.6f)));
            ItemRenderManager.instance().render(itemStack, 0);
            graphics.popModelMatrix();
            graphics.disableGamma();
        });
    }
}

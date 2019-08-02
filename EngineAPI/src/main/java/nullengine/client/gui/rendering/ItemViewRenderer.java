package nullengine.client.gui.rendering;

import nullengine.client.gui.component.ItemView;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.shader.ShaderManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class ItemViewRenderer implements ComponentRenderer<ItemView> {

    public static final ItemViewRenderer INSTANCE = new ItemViewRenderer();

    @Override
    public void render(ItemView component, Graphics graphics, RenderContext context) {
        Optional<ItemRenderManager> optionalItemRenderManager = context.getComponent(ItemRenderManager.class);
        if (optionalItemRenderManager.isPresent()) {
            ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f().translationRotateScale(
                    new Vector3f(component.viewSize().get() * 0.5f, component.viewSize().get() * 0.5f, 0),
                    new Quaternionf(new AxisAngle4f()).rotateAxis((float) -(Math.PI / 4), 0, 1, 0).rotateAxis((float) -Math.PI / 6f, 1, 0, -1),
                    new Vector3f(component.viewSize().get() * 0.6f,-component.viewSize().get() * 0.6f,component.viewSize().get() * 0.6f)));
            ShaderManager.instance().setUniform("u_EnableGamma", true);
            optionalItemRenderManager.get().render(component.item().getValue(), 0);
            ShaderManager.instance().setUniform("u_EnableGamma", false);
            ShaderManager.instance().setUniform("u_ModelMatrix", new Matrix4f());
        }
    }
}

package nullengine.client.gui.rendering;

import nullengine.client.gui.component.TextureItem;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.item.ItemRenderManager;
import nullengine.client.rendering.shader.ShaderManager;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class TextureItemRenderer implements ComponentRenderer<TextureItem> {

    public static final TextureItemRenderer INSTANCE = new TextureItemRenderer();

    @Override
    public void render(TextureItem component, Graphics graphics, RenderContext context) {
        Optional<ItemRenderManager> optionalItemRenderManager = context.getComponent(ItemRenderManager.class);
        if (optionalItemRenderManager.isPresent()) {
            ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f().translationRotateScale(new Vector3f(component.imgLength().get() * 0.08f, component.imgLength().get() * 0.75f, 0), new Quaternionf(new AxisAngle4f(3.141592625f, 1, 0, 0)).rotateAxis((float) (Math.PI / 4), 0, 1, 0).rotateAxis((float) Math.PI / 6f, (float) Math.cos(Math.PI / 4), 0, (float) Math.cos(Math.PI / 4)), component.imgLength().get() * 0.6f));
            optionalItemRenderManager.get().render(component.item().getValue(), 0);
            ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f());
        }
    }
}

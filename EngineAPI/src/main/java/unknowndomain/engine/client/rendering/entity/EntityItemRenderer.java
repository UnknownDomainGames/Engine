package unknowndomain.engine.client.rendering.entity;

import org.joml.Matrix4f;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.client.rendering.item.ItemRenderManager;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.entity.item.EntityItem;

public class EntityItemRenderer implements EntityRenderer<EntityItem> {

    private RenderContext context;

    @Override
    public void init(RenderContext context) {
        this.context = context;
    }

    @Override
    public boolean shouldRender(EntityItem entity, double x, double y, double z, float partial) {
        return true;
    }

    @Override
    public void render(EntityItem entity, double x, double y, double z, float partial) {
        ShaderManager.INSTANCE.setUniform("u_ModelMatrix", new Matrix4f()
                .translate((float) x, (float) y + .5f - .5f * (float) Math.sin(Math.toRadians((entity.getWorld().getGameTick() + partial) * 10)), (float) z)
                .scale(1f / 3, 1f / 3, 1f / 3)
                .rotateY(((int) entity.getWorld().getGameTick() % 360000) / 20f)
                .translate(-.5f, 0, -.5f));

        context.getComponent(ItemRenderManager.class).ifPresent(itemRenderManager -> itemRenderManager.render(entity.getItemStack(), partial));
    }

    @Override
    public void dispose() {

    }
}

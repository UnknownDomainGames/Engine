package unknowndomain.engine.client.rendering.world;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.rendering.game3d.Game3DRenderer.GameRenderEnv;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.util.Color;

public class BlockSelectionRenderer {

    private GameRenderEnv env;

    public void init(GameRenderEnv env) {
        this.env = env;
    }

    public void render(float partial) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        var player = env.getGame().getPlayer();
        var camera = env.getContext().getCamera();
        RayTraceBlockHit hit = player.getWorld().raycast(camera.getPosition(), camera.getFrontVector(), 10);
        if (hit.isSuccess()) {
            float minX = hit.getPos().getX() - 0.001f, maxX = hit.getPos().getX() + 1.001f,
                    minY = hit.getPos().getY() - 0.001f, maxY = hit.getPos().getY() + 1.001f,
                    minZ = hit.getPos().getZ() - 0.001f, maxZ = hit.getPos().getZ() + 1.001f;
            buffer.begin(GL11.GL_LINES, true, true, false);
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
            tessellator.draw();
        }
    }

    public void dispose() {

    }
}

package unknowndomain.engine.client.rendering.world;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.util.Color;
import unknowndomain.engine.util.Disposable;

public class BlockSelectionRenderer implements Disposable {

    public void render(float partial) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RayTraceBlockHit hit = null;
        if (hit != null) {
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

    @Override
    public void dispose() {

    }
}

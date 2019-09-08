package nullengine.client.rendering.world;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.client.rendering.util.buffer.GLBufferFormats;
import nullengine.client.rendering.util.buffer.GLBufferMode;
import nullengine.util.Color;
import nullengine.world.collision.RayTraceBlockHit;

public class BlockSelectionRenderer {

    private RenderManager context;

    public void init(RenderManager context) {
        this.context = context;
    }

    public void render(float partial) {
        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();

        var player = context.getEngine().getCurrentGame().getPlayer();
        var camera = context.getCamera();
        RayTraceBlockHit hit = player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10);
        if (hit.isSuccess()) {
            float minX = hit.getPos().getX() - 0.001f, maxX = hit.getPos().getX() + 1.001f,
                    minY = hit.getPos().getY() - 0.001f, maxY = hit.getPos().getY() + 1.001f,
                    minZ = hit.getPos().getZ() - 0.001f, maxZ = hit.getPos().getZ() + 1.001f;
            buffer.begin(GLBufferMode.LINES, GLBufferFormats.POSITION_COLOR);
            buffer.pos(minX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();

            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();

            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();

            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).uv(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).uv(0, 0).endVertex();
            tessellator.draw();
        }
    }

    public void dispose() {

    }
}

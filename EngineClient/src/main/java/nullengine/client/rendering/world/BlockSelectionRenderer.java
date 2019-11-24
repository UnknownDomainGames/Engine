package nullengine.client.rendering.world;

import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.gl.GLDrawMode;
import nullengine.client.rendering.gl.GLVertexFormats;
import nullengine.client.rendering.texture.TextureManager;
import nullengine.util.Color;
import nullengine.world.hit.BlockHitResult;

public class BlockSelectionRenderer {

    private RenderManager context;

    public void init(RenderManager context) {
        this.context = context;
    }

    public void render(float partial) {
        Tessellator tessellator = Tessellator.getInstance();
        GLBuffer buffer = tessellator.getBuffer();

        var player = context.getEngine().getCurrentGame().getClientPlayer();
        var camera = context.getCamera();
        BlockHitResult hit = player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10);
        if (hit.isSuccess()) {
            float minX = hit.getPos().x() - 0.001f, maxX = hit.getPos().x() + 1.001f,
                    minY = hit.getPos().y() - 0.001f, maxY = hit.getPos().y() + 1.001f,
                    minZ = hit.getPos().z() - 0.001f, maxZ = hit.getPos().z() + 1.001f;
            buffer.begin(GLDrawMode.LINES, GLVertexFormats.POSITION_COLOR);
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
            TextureManager.instance().getWhiteTexture().bind();
            tessellator.draw();
        }
    }

    public void dispose() {

    }
}

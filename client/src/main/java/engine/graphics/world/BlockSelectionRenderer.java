package engine.graphics.world;

import engine.client.asset.Asset;
import engine.client.asset.AssetTypes;
import engine.graphics.RenderManager;
import engine.graphics.gl.GLStreamedRenderer;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;
import engine.world.hit.BlockHitResult;

public final class BlockSelectionRenderer {

    private final RenderManager manager;

    private final Asset<Texture2D> whiteTexture;

    public BlockSelectionRenderer(RenderManager manager) {
        this.manager = manager;
        this.whiteTexture = Asset.create(AssetTypes.TEXTURE, "buildin", "white");
    }

    public void render(float tpf) {
        GLStreamedRenderer directRenderer = GLStreamedRenderer.getInstance();
        VertexDataBuf buffer = directRenderer.getBuffer();

        var player = manager.getEngine().getCurrentGame().getClientPlayer();
        var camera = manager.getViewport().getCamera();
        BlockHitResult hit = player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10);
        if (hit.isSuccess()) {
            float minX = hit.getPos().x() - 0.001f, maxX = hit.getPos().x() + 1.001f,
                    minY = hit.getPos().y() - 0.001f, maxY = hit.getPos().y() + 1.001f,
                    minZ = hit.getPos().z() - 0.001f, maxZ = hit.getPos().z() + 1.001f;
            buffer.begin(VertexFormat.POSITION_COLOR);
            buffer.pos(minX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();

            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();

            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, maxY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();

            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(minX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, maxY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, minY, maxZ).color(Color.WHITE).tex(0, 0).endVertex();
            buffer.pos(maxX, minY, minZ).color(Color.WHITE).tex(0, 0).endVertex();
            whiteTexture.get().bind();
            directRenderer.draw(DrawMode.LINES);
        }
    }

    public void dispose() {

    }
}

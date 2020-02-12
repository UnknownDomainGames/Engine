package engine.graphics.voxel.shape;

import engine.client.asset.Asset;
import engine.client.asset.AssetTypes;
import engine.graphics.Drawable;
import engine.graphics.mesh.SingleBufferMesh;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;

public class SelectedBlock implements Drawable {

    private final Asset<Texture2D> texture;
    private final SingleBufferMesh mesh;

    public SelectedBlock() {
        texture = Asset.create(AssetTypes.TEXTURE, "buildin", "white");
        float minX = -0.001f, maxX = 1.001f,
                minY = -0.001f, maxY = 1.001f,
                minZ = -0.001f, maxZ = 1.001f;
        VertexDataBuf buf = VertexDataBuf.currentThreadBuffer();
        buf.begin(VertexFormat.POSITION_COLOR);
        buf.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, minY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();

        buf.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(minX, maxY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

        buf.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(minX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, maxY, minZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();

        buf.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(minX, minY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, maxY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, minY, maxZ).color(Color.WHITE).endVertex();
        buf.pos(maxX, minY, minZ).color(Color.WHITE).endVertex();
        buf.finish();

        mesh = SingleBufferMesh.builder().drawMode(DrawMode.LINES).build(buf);
    }

    @Override
    public void draw() {
        texture.get().bind();
        mesh.draw();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}

package engine.graphics.voxel.shape;

import engine.graphics.Geometry;
import engine.graphics.mesh.SingleBufMesh;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexDataBuf;
import engine.graphics.vertex.VertexFormat;
import engine.util.Color;

public class SelectedBlock extends Geometry {

    public SelectedBlock() {
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

        setMesh(SingleBufMesh.builder().drawMode(DrawMode.LINES).build(buf));
    }
}
